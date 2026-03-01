FROM gitlab/gitlab-ee:18.9.1-ee.0

COPY scan.features.rb /etc/gitlab/license/scan.features.rb

WORKDIR /etc/gitlab/license
RUN apt-get update && \
    apt-get install ruby-full dos2unix -y && \
    gem install bundler && \
    gem install gitlab-license && \
    git clone https://github.com/Lakr233/GitLab-License-Generator.git && \
    cd GitLab-License-Generator && \
    cp /etc/gitlab/license/scan.features.rb src/scan.features.rb && \
    dos2unix src/scan.features.rb && \
    chmod +x src/scan.features.rb && \
    chmod +x make.sh && \
           LICENSE_NAME="Home Lab" \
           LICENSE_COMPANY="Home Lab" \
           LICENSE_EMAIL="emilyyy0621@gmail.com" \
           LICENSE_PLAN="ultimate" \
           LICENSE_USER_COUNT="2147483647" \
           LICENSE_EXPIRE_YEAR="2100" \
           ./make.sh && \
    cp build/public.key /opt/gitlab/embedded/service/gitlab-rails/.license_encryption_key.pub && \
    cp build/result.gitlab-license /etc/gitlab/home-lab.gitlab-license
