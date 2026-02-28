package atlassian;

//import io.zhile.crack.atlassian.agent.KeyTransformer;
import net.bytebuddy.asm.Advice;
import net.bytebuddy.implementation.bytecode.assign.Assigner;
import net.bytebuddy.matcher.ElementMatchers;
import net.bytebuddy.agent.builder.AgentBuilder;

import java.io.*;

import java.lang.invoke.MethodType;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import java.lang.instrument.Instrumentation;

import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SignatureException;
import java.time.ZoneId;
import java.time.LocalDate;
import java.text.SimpleDateFormat;

import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.logging.Logger;

import static java.lang.invoke.MethodHandles.lookup;
import static java.lang.invoke.MethodHandles.privateLookupIn;
import static net.bytebuddy.matcher.ElementMatchers.*;

public class Agent {

    public static final Logger logger = Logger.getLogger(Agent.class.getSimpleName());

    public static Object service;

    public static void premain(String args, Instrumentation inst) {
        new AgentBuilder.Default()
                .type(nameEndsWith("impl.setup.LicenseServiceBootstrapAppConfig"))
                .transform((builder, typeDescription, classLoader, module, protectionDomain) ->
                        builder.visit(Advice.to(new Object(){
                                    @Advice.OnMethodExit
                                    public static void onExit(@Advice.Return Object licenseService) {
                                        service = licenseService;
                                        logger.info("License service is ready.");
                                    }
                                }.getClass())
                                .on(isMethod().and(named("licenseService")))))

                .type(named("com.atlassian.extras.keymanager.Key"))
                .transform((builder, typeDescription, classLoader, module, protectionDomain) ->
                        builder
                                .constructor(takesArguments(3))
                                .intercept(Advice.to(new Object() {
                                    @Advice.OnMethodEnter
                                    public static void onEnter(
                                            @Advice.Argument(value = 0, readOnly = false) String key,
                                            @Advice.Argument(1) Object version,
                                            @Advice.Argument(2) Object type
                                    ) {
                                        if (type.toString().equals("PUBLIC")) {
                                            logger.info("Injecting public key " + version);
                                            key = LicenseManager.publicKeyBase64;
                                        }
                                    }
                                }.getClass()))
                )

                .type(named("com.atlassian.extras.api.Product"))
                .transform((builder, typeDescription, classLoader, module, protectionDomain) ->
                        builder.visit(Advice.to(new Object(){
                            @Advice.OnMethodExit
                            public static void onExit(@Advice.This Object product,
                                                      @Advice.Argument(0) Object other,
                                                      @Advice.Return Boolean returned) {
                                if (other.toString().equals("#{T(com.atlassian.extras.api.Product).CONFLUENCE}")) {
                                    logger.info("Installing product license.");
                                    try {
                                        service
                                                .getClass()
                                                .getDeclaredMethod("install", String.class)
                                                .invoke(service, LicenseManager.generateLicense());
                                    } catch (Exception e) {
                                        logger.severe("Failed to install license: " + e.getMessage());
                                        e.printStackTrace();
                                    }
                                } else {
                                    logger.info("Checking product: " + other);
                                }
                            }
                        }.getClass()).on(isEquals()))
                )

                .type(named("com.atlassian.extras.common.util.ProductLicenseProperties"))
                .transform((builder, typeDescription, classLoader, module, protectionDomain) ->
                        builder.visit(Advice.to(new Object(){
                                    @Advice.OnMethodExit
                                    public static void onExit(
                                            @Advice.Argument(value = 0) String key,
                                            @Advice.Return(readOnly = false) String value) {
                                        String[] property = key.split("\\.");
                                        String propertyName = property[property.length - 1];

                                        switch (propertyName) {
                                            case "Description",
                                                 "PartnerName",
                                                 "Organisation",
                                                 "ContactEMail",
                                                 "ContactName",
                                                 "CreationDate",
                                                 "PurchaseDate",
                                                 "GracePeriod",
                                                 "Evaluation",
                                                 "SEN",
                                                 "NumberOfClusterNodes" -> {}
                                            case "active", "enterprise", "Subscription", "DataCenter" -> value = "true";
                                            case "LicenseTypeName" -> value = "COMMERCIAL";
                                            case "LicenseEdition" -> value = "UNLIMITED";
                                            case "ServerID" -> value = "Default";
                                            case "LicenseExpiryDate", "MaintenanceExpiryDate" ->
                                                    value = new SimpleDateFormat("yyyy-MM-dd").format(
                                                            Date.from(
                                                                    LocalDate
                                                                            .now()
                                                                            .plusYears(100)
                                                                            .atStartOfDay(ZoneId.systemDefault())
                                                                            .toInstant()
                                                            )
                                                    );
                                            case "NumberOfUsers" -> value = String.valueOf(-1);
                                            default -> logger.warning("Unknown license property: " + key);
                                        }
                                    }
                                }.getClass()).on(named("getProperty").and(takesArguments(1)))))

                .type(named("com.atlassian.upm.license.internal.impl.PluginLicenseRepositoryImpl"))
                .transform((builder, typeDescription, classLoader, module, protectionDomain) ->
                        builder.visit(Advice.to(new Object(){
                                    @Advice.OnMethodExit
                                    public static void onExit(
                                            @Advice.This Object self,
                                            @Advice.Origin Method method,
                                            @Advice.Argument(0) String pluginKey,
                                            @Advice.Return(readOnly = false, typing = Assigner.Typing.DYNAMIC) Object returned
                                    ) {
                                        try {
                                            Object isDefined = returned.getClass().getMethod("isDefined").invoke(returned);
                                            if (Boolean.FALSE.equals(isDefined)) {
                                                Class<?> licenseManager = Class.forName("atlassian.LicenseManager", false, ClassLoader.getSystemClassLoader());

                                                System.out.println("Plugin " + pluginKey + " doesn't have license. Installing it now.");

                                                Object success = self
                                                        .getClass()
                                                        .getMethod("setPluginLicense", String.class, String.class)
                                                        .invoke(
                                                                self,
                                                                pluginKey,
                                                                String.valueOf(
                                                                        privateLookupIn(licenseManager, lookup()).findStatic(
                                                                                licenseManager,
                                                                                "generateLicense",
                                                                                MethodType.methodType(String.class)
                                                                        ).invoke()
                                                                ));

                                                if (Boolean.TRUE.equals(success.getClass().getMethod("isLeft").invoke(success))) {
                                                    System.out.println("Failed to install license for " + pluginKey + " plugin");
                                                } else returned = method.invoke(self, pluginKey);
                                            }
                                        } catch (Throwable t) {
                                            System.out.println("Reflection failed: " + t);
                                        }
                                    }
                                }.getClass()).on(named("getPluginLicense").and(takesArguments(String.class)))
                        )
                )
                .installOn(inst);

        new AgentBuilder.Default()
                .type(nameContains("com.atlassian.jira.license.JiraLicenseManagerImpl")).transform(
                        (builder, typeDescription, classLoader, module, protectionDomain) ->
                                builder.visit(Advice.to(new Object() {
                                    @Advice.OnMethodEnter
                                    public static void onEnter(
                                            @Advice.Argument(2) Object multiLicenseStore
                                    ) {
                                        System.out.println("STORE!!");
                                        try {
                                            multiLicenseStore.getClass()
                                                    .getDeclaredMethod("store", Iterable.class)
                                                    .invoke(multiLicenseStore, List.of(LicenseManager.generateLicense()));
                                        } catch (Exception e) {
                                            e.printStackTrace();
                                        }
                                    }
                                }.getClass()).on(isConstructor())))

                .type(nameContains("com.atlassian.jira.web.action.setup.SetupLicense")).transform(
                        (builder, typeDescription, classLoader, module, protectionDomain) ->
                                builder.visit(Advice.to(new Object() {
                                    @Advice.OnMethodEnter
                                    public static void onEnter(
                                            @Advice.Argument(value = 0, readOnly = false) String licenseString
                                    ) {
                                        try {
                                            licenseString = LicenseManager.generateLicense();
                                        } catch (Exception e) {
                                            e.printStackTrace();
                                        }
                                    }
                                }.getClass()).on(named("setSetupLicenseKey"))))

                .type(named("com.atlassian.jira.license.MultiLicenseStoreImpl")).transform(
                        (builder, typeDescription, classLoader, module, protectionDomain) ->
                                builder.visit(Advice.to(new Object() {
                                    @Advice.OnMethodExit
                                    public static void onExit(
                                            @Advice.This Object self,
                                            @Advice.Return(readOnly = false, typing = Assigner.Typing.DYNAMIC) Object returned
                                    ) {
                                        try {
                                            if (returned == null) {
                                                returned = "Default";
                                                self.getClass().getMethod("storeServerId", String.class).invoke(self, returned.toString());
                                            }
                                        } catch (Exception e) {
                                            e.printStackTrace();
                                        }
                                    }
                                }.getClass()).on(named("retrieveServerId"))))
                .installOn(inst);
//        new AgentBuilder.Default()
//                .type(ElementMatchers.not(ElementMatchers.nameContains(".plugin.")).and(
//                        ElementMatchers.nameStartsWith("com.atlassian.extras.decoder")
//                        .or(nameContains("com.atlassian.extras.keymanager"))
//                        .or(nameContains("icense"))
//                        .or(nameContains("com.atlassian.jira.web.action.setup."))
//                ))
//
//                .transform((builder, type, classLoader, module, protectionDomain) ->
//                        builder.visit(
//                                Advice.to(LogAdvice.class)
//                                        .on(ElementMatchers.isMethod()
//                                                .and(ElementMatchers.not(ElementMatchers.isAbstract()))
//                                                .and(ElementMatchers.not(ElementMatchers.isNative()))
//                                        )
//                        )
//                )
//                .installOn(inst);
    }

    public class LogAdvice {

        @Advice.OnMethodEnter
        static void enter(
                @Advice.This Object self,
                @Advice.Origin Method method) {

            Class<?> clazz = self.getClass();
            try {

                var source = clazz.getProtectionDomain()
                        .getCodeSource();

                if (source != null && source.getLocation() != null) {
                    System.out.println(
                            "[CLASS-LOCATION] "
                                    + clazz.getName()
                                    + " -> "
                                    + source.getLocation()
                    );
                } else {
                    System.out.println(
                            "[CLASS-LOCATION] "
                                    + clazz.getName()
                                    + " -> <no code source> (generated or bootstrap)"
                    );
                }
            } catch (Throwable t) {
                t.printStackTrace();
            }
            System.out.println("[ENTER] " + method);
        }

        @Advice.OnMethodExit(onThrowable = Throwable.class)
        static void exit(
                @Advice.Origin Method method,
                @Advice.Return(readOnly = false, typing = Assigner.Typing.DYNAMIC) Object ret,
                @Advice.Thrown Throwable t) {
            if (t != null) {
                System.out.println("[THROW] " + method + " ex=" + t);
                t.printStackTrace();
            } else {
                System.out.println("[EXIT ] " + method + " ret=" + ret);
            }
        }
    }
}
