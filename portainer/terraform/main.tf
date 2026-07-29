terraform {
  required_providers {
    portainer = {
      source  = "portainer/portainer"
    }
  }
}

variable "portainer_api_key" {
  type      = string
  sensitive = true
}

variable "docker_api_token" {
  type      = string
  sensitive = true
}

variable "artifactory_api_token" {
  type      = string
  sensitive = true
}

provider "portainer" {
  endpoint = "https://portainer.lab.eemilee.me"
  api_key  = var.portainer_api_key
}

resource "portainer_environment" "swarm_cluster" {
  name                 = "Swarm Cluster"
  environment_address  = "tcp://192.168.0.202:9001"
  type                 = 2
  group_id             = 1
}

resource "portainer_registry" "dockerhub" {
  name     = "Docker Hub"
  type     = 6
  url      = "docker.io"
  username = "emilyyy0621"
  password = var.docker_api_token
}

resource "portainer_registry" "artifactory" {
  name           = "JFrog Artifactory"
  url            = "artifactory.lab.eemilee.me"
  type           = 3
  authentication = true
  username       = "admin"
  password       = var.artifactory_api_token
}