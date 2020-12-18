
 
## 1 Introduction

Sunbird is an open source repository of learning management microservices architected for scale and designed to support diverse teaching and learning solutions. Sunbird is the open source contribution of the EkStep Foundation and is licensed under the MIT licence.

Sunbird software is containerized. The installation script uses the Kubernetes orchestration engine to run the Sunbird docker images. The Kubernetes consists of manager and agent nodes. The pods run on the agent nodes and the manager nodes manage the pods lifecycle. 

Services like Portal, LMS Backend, API Gateway and Proxies etc., are run as Kubernetes pods. Services like learning, Search, Keycloak, Cassandra, PostgreSql, Elasticsearch etc., are run on Virtual Machines (VMs) directly.

This document explains the procedure to set up Sunbird on your cloud infra. This installation has been tested with:

* AKS(azure kubernetes service) cluster
* 6 VMs running the vanilla Ubuntu 16 image;
* a Public IP
* key based ssh possible to the machines

## Infrastructure Components

Sunbird consists of 3 major subsystems

  - Knowledge Platform also knows as Learning Platform (Taxonomy, Content and Content Management)
  - Data-pipeline (Creating insights from telemetry)
  - Core (User, Organization, Courses, Badges, Content Studio)


---
## 2 Prerequisites

## Overview

Before you install Sunbird in a server environment, make sure that you have the required permissions, servers, hardware, software, accounts and so on. Without these aspects in place, you may face delays that can best be avoided.

### Recommended Permissions and Experience

To efficiently handle Sunbird installation, you need to have:
- System administrator permissions on Sunbird and all servers
- Hands-on experience in administering Linux systems
- Hands-on experience using Docker and Kubernetes to run containerized services

> **Note:** Sunbird is tested on cloud hosted Linux servers (Azure & AWS). Hence, it is recommended that you use Linux servers. Sunbird and its functionality is not tested on Microsoft® operating systems, or on in-premise infrastructure, like rack mounted servers. The Sunbird installer has a known issue on virtual machines such as those created by VirtualBox or VMWare. 

## Provisioning Servers 
Before you start the installation process, ensure that you provision for servers to host applications and set up required accounts and repositories as per details provided.

|Application|  Server           |Count|
|-----------|-------------------|-----|  
|Jenkins    | 4core 16G 250G HDD | 1   |
| KP        | 4core 16G 60G HDD | 1   |
| DP        | 4core 16G 60G HDD | 1   |
| DB        | 4core 16G 60G HDD   | 1   |
| Yarn      | 4core 16G 60G HDD | 2   |
| Druid     | 4core 16G 60G HDD | 1  |
| Load Balancers         |  -   | 2 (Optional)   |

## List of Servers with their Ansible Group Name
<table>
  <tr>
    <th style="width:25%">Module</th>
    <th style="width:25%">Servers</th>
    <th style="width:25%">Service</th>
    <th style="width:25%">Ansible Group Name</th>
  </tr>
  <tr>
    <td>Build and Deploy</td>
    <td>Server-1 (Jenkins)</td>
    <td>Jenkins Master</td>
    <td></td>
  </tr>
  <tr>
    <td rowspan="4">Databases</td>
    <td rowspan="4">Server-2 (DB's)</td>
    <td>Cassandra</td>
    <td>cassandra-1, lp-cassandra, dp-cassandra, core-cassandra, cassandra-node-1, cassandra-ps, cassandra</td>
  </tr>
  <tr>
    <td>Postgres</td>
    <td>postgresql-master-1, postgresql-slave-1, postgres</td>
  </tr>
  <tr>
    <td>Application Elasticsearch</td>
    <td>es-1, composite-search-cluster, es-ps, core-es-1</td>
  </tr>
  <tr>
    <td>Neo4j</td>
    <td>learning-neo4j-node1</td>
  </tr>
  <tr>
    <td rowspan="4">Knowledge Platform</td>
    <td rowspan="4">Server-3 (KP Services and Kafka)</td>
    <td>Learning</td>
    <td>learning1, logstash-ps, learning</td>
  </tr>
  <tr>
    <td>Redis</td>
    <td>redis1, lp-redis, redis, dp-redis, lms-redis</td>
  </tr>
  <tr>
    <td>dial</td>
    <td>dial1</td>
  </tr>
  <tr>
    <td>Kafka</td>
    <td>processing-cluster-kafka, processing-cluster-zookeepers, kafka-ps, kafka-1, ingestion-cluster-kafka</td>
  </tr>
  <tr>
    <td rowspan="8">Data Pipeline</td>
    <td rowspan="8">Server-4 (DP Services)</td>
    <td>Spark</td>
    <td>spark</td>
  </tr>
  <tr>
    <td>Analytics</td>
    <td>analytics-api, analytics-ps</td>
  </tr>
  <tr>
    <td>Kafka Indexer</td>
    <td>kafka-indexer</td>
  </tr>
  <tr>
    <td>InfluxDB</td>
    <td>influxdb</td>
  </tr>
  <tr>
  <td>Docker Manager and Worker</td>
    <td>swarm-manager-1, swarm-agent-for-prometheus-1, swarm-agent-for-alertmanager-1, swarm-bootstrap-manager, swarm-node-1 , swarm-agent-dashboard-1, swarm-dashboard-1</td>
  </tr>
  <tr>
    <td>Keycloak</td>
    <td>Keycloak</td>
  </tr>
  <tr>
    <td>Logs Elasticsearch</td>
    <td>log-es-1</td>
  </tr>
    <td>Secor</td>
    <td>secor, secor-ps</td>
  </tr>
  <tr>
    <td rowspan="2">Yarn</td>
    <td>Server-5 (Yarn Master)</td>
    <td>Yarn Master Slave 1</td>
    <td>yarn-master, yarn-slave, yarn-ps</td>
  </tr>
  <tr>
    <td>Server-6 (Yarn Slave)</td>
    <td>Yarn Master Slave 2</td>
    <td>yarn-master, yarn-slave, yarn-ps</td>
  </tr>
  <tr>
    <td>Druid</td>
    <td>Server-7</td>
    <td>druid servicer</td>
    <td> druid-postgres,raw-coordinator,raw-overlord,raw-broker,raw-historical,raw-middlemanager,raw-graphite,raw-zookeeper
    </td>
   </tr> 
</table>



## Infra Requirements

1.k8s Cluster  
2.Private GitHub repository to store Ansible hosts and secrets  
3.Fully Qualified Domain Name (FQDN) with SSL  
4.Azure Storage account   
5.Docker hub account   
6.A Public IP  
7.Security:  
- All ports must be open in internal networks (Azure-Vnet or AWS-VPC) for internal comumnication between the VMs
- By default, all the outbound ports are allowed for public access from the VM. 

## Steps to create AKS cluster

> **Note**  below steps is for creating Kuebrneets cluster in Azure, Please refer respective cloud providers document for any other cloud.
AKS cluster and vm's should be in same vnet (if both are in diffrent vnet, vnet peering has to be done)  to do  vnet peering bot the vnet ip's should not overlap. 

1.command to create aks cluster: (requires az cli and aks-preview)

 ```
    - create service principal and assign contributor role to service principal, get the secrets and client id of service principal. (https://docs.microsoft.com/en-us/cli/azure/create-an-azure-service-principal-azure-cli)
    
    - az aks create --resource-group <resouse-group-name> --node-resource-group <k8s-resource-group-name> --name <cluster name>  --node-count 4 --admin-username deployer --kubernetes-version 1.16.13 --service-principal "<service principal id>" --node-vm-size <vm size> --client-secret "<client id>" --network-plugin azure --ssh-key-value @deployer.pub -l <region> --vm-set-type VirtualMachineScaleSets --vnet-subnet-id /subscriptions/<subscription id>/resourceGroups/<resouse-group-name>/providers/Microsoft.Network/virtualNetworks/<vnet-name>/subnets/<subnet name>

    - command to get kube config file for created cluster:
       az aks get-credentials --resource-group <resource group name> --name <cluster name> --file - > k8s.yaml

 ```  

 ## Azure Storage account configuration

 1.Update CORS rule for storage account as below

 ```
    Allowed Origins: *
    Allowed Methods: GET,HEAD,OPTIONS, PUT
    Allowed Headers: Access-Control-Allow-Method,Origin,x-ms-meta-qqfilename,x-ms-blob-type,x-ms-blob-content-type,Content-Type
    Exposed Headers: Access-Control-Allow-Origin,Access-Control-Allow-Methods
    Max Age: 200

 ``` 

 2.Disable Secure transfer required in stoarge account configuration

 3.Create the following containers in Storage account with public ACL (dial, termsandcondtions, content)


---
## 3 Load Balancer Setup

## Overview

Load Balancers are required to scale your applications and create a high availability for your services. Balancing the load provides
low latency and high throughput, and scales up to millions of flows for all TCP and UDP applications.

The Load Balancer distributes new inbound flows that arrive on its frontend to backend pool instances, as per rules and health probes that are set. This page provides details to set up the Agent Swarm, Keycloak Swarm, KP-LB services and the DP-LB services.

**Note:** The load balancers can be layer 4 or layer 7.

## Keycloak

To setup Keycloak Swarm, execute the following instructions for each of the mentioned fields:  
- Frontend IP configuration - Internal IP (default)
- Backend pools - attach keycloak vm or availability set of keycloak group
- Health Probes/check - Configure path and port - 8080  
    Example: name: keycloakhealth 
- Protocol: TCP 
- Port: 8080 
- Interval: 5 
- Unhealthy threshold: 2
- Load Balancing rules - Frontend-ip-config, Frontend-port, backend-port, Backend-pool and health-probe  
    Example: Frontend-port: 80, Backend-port: 8080


## Knowledge Platform 

**Learning:**

To setup KP-LB services, execute the following instructions for each of the mentioned fields: 
- Frontend IP configuration - Internal IP (default)
- Backend pools - attach vm's of learning and search or availability set for learning and search
- Health Probes/check - Configure path and port - 8080 (for learning) and 9000 (for search)  
    Example: name: learninghealth 
- Protocol: http 
- Port: 8080 
- Path: /learning-service/health
- Interval: 5 
- Unhealthy threshold: 2
- Frontend-port: 8080
- Backend-port: 8080

## 4 Ansible Variables Setup

## Overview

Sunbird uses Ansible playbooks to manage its three major sub-systems (Knowledge Platform, Data Pipeline and Core Services), to ensure scalability, consistency and reliability of its IT environment. The Ansible variables help server setup, configuration management and automate deployment.   

    
## Updating the Private Repository with Hosts and Variables

Use the following Git commands sequentially to clone and update your private GitHub repository: 

1. `git clone` <a href="https://project-sunbird/sunbird-devops">https://github.com/project-sunbird/sunbird-devops</a>

2. `cd sunbird-devops; git checkout tags/release-3.4.0 -b release-3.4.0`

3. Copy the directory `sunbird-devops/private_repo/ansible` to your private repo location

4. Update the files `common.yml`, `hosts`, `secrets.yml` under `Core`, `KnowledgePlatform` and `DataPipeline`. After updating, push them to your private repo branch. The structure under the `ansible` directory is shown below.


> **Note** The following depicts the folder structure required in the private GitHub repository that contains Ansible hosts, secrets and variables.
  
```
ansible
└── inventory
    └── dev
        ├── Core
        │   ├── common.yml
        │   ├── hosts
        │   └── secrets.yml
        ├── DataPipeline
        │   ├── common.yml
        │   ├── hosts
        │   └── secrets.yml
        └── KnowledgePlatform
            ├── common.yml
            ├── hosts
            └── secrets.yml
```

## 5. Jenkins Setup

## Overview
This page provides you with step-by-step instructions to set up the Jenkins server. This is the first step in the Sunbird installation process.

## Jenkins Setup

1.SSH to the Jenkins server. Enter the following commands:

 ```
    - git clone [https://github.com/project-sunbird/sunbird-devops.git](https://github.com/project-sunbird/sunbird-devops.git) 
    - cd sunbird-devops && git checkout tags/release-3.4.0 -b release-3.4.0
    - cd deploy/jenkins
    - sudo bash jenkins-server-setup.sh

 ```   
        
2.After running the `jenkins-server-setup.sh` script, open Jenkins in a browser by typing **domain-name:8080 / public-ip:8080**

3.Enter the initial password and follow the on-screen instructions

4.Choose **Install suggested plugin** 

5.Create an admin user 

6.Choose the default Jenkins URL. You can either change this to your domain name or public IP. If in doubt, use the name displayed on the screen, as it can be changed later if required, in the Jenkins configuration

7.Switch back to the terminal session on the Jenkins server. Enter the following command, which will install the required plugins:

    sudo bash jenkins-plugins-setup.sh
   
8.Go to **Manage Jenkins** -> **Manage Plugins** -> **Update Center** -> **Check status of plugin install**. If any plugins have not been installed, install them manually. To do so, go to the plugins section of Jenkins

9.Switch back to the terminal session on the Jenkins server
    
    cp envOrder.txt.sample envOrder.txt 
    vi envOrder.txt  (Note: you can use :wq after editing the file to save and quit)
    

10.Update the environment list as per your infrastructure in ascending order. For example, if you have only dev and production, your **envOrder.txt**will look like:

    dev=0
    production=1 

11.Run the `jenkins-jobs-setup.sh` script, using the command,`sudo bash jenkins-jobs-setup.sh`

12.Follow the on-screen instructions. Enter **Yes** for all questions, when prompted for a choice. 

> **Note** The options are case sensitive, the script displays the accepted options 
 
13.After the script completes copying the job configurations, go to the browser and restart Jenkins using the command, `public-ip:8080/restart` OR `domain-name:8080/restart` 
 
14.Go to **http://(jenkins_domain)/credentials/store/system/domain/_/newCredentials** 
 
15.Select **Username with Password** in Kind drop down and enter the username and password in the respected field of the GitHub account where the private repository is hosted
 
16.Enter a unique long string for the ID field, for example,**private-repo-creds** 
 
17.Specify the description for the repository, for example,**private repo credentails**, and click **OK**
 
18.Go to **http://(jenkins_domain)/configure** 
    
19.Select the **Environment Variables** check box
 
20.Click **Add** and enter the following Name, Value pairs: 
 
|**Name**|**Value**| 
|---|---| 
|**ANSIBLE_FORCE_COLOR**|true| 
|**ANSIBLE_HOST_KEY_CHECKING**|false| 
|**ANSIBLE_STDOUT_CALLBACK**|debug| 
|**hub_org**|docker hub organization / username. Example: In sunbird/player image, sunbird is the hub_org| 
|**private_repo_branch**|The branch name in the private repository which you would like to use. This branch has the inventory and secrets| 
|**private_repo_credentials**|The unique string which you provided for ID field while entering the GitHub repo credentials. Example: **private-repo-creds**| 
|**private_repo_url**|The GitHub URL to your private repo. To get the URL of your private repository, go to your private repository and click the **Clone** button. The https URL of your private repository is displayed. Currently, Sunbird supports only https URLs| 
|**public_repo_branch**|This is the branch or tag to pick the Jenkins file. You can set this value as refs/tags/release-3.4.0 if you want to build from tags or provide the value of the development branch, for example, release-3.4.0. Changing this value is not recommended since development branches are not stable. |
|**override_private_branch**|true|
|**override_public_branch**|true| 
|**java11_home**|/usr/lib/jvm/java-11-openjdk-amd64/|
 
21.Scroll to the **Global Pipeline Libraries** section and click **Add**. Provide the values as below:

|**Name**|**Value**| 
|-------|--------| 
|**Library Name**|deploy-conf| 
|**Default version**|Tag name of the Jenkins shared library. This should be the same version of the release you are going to build and deploy. For example, if you decide to use tag release-3.4.0 as your base, the Jenkins shared library tag is shared-lib. When you upgrade to tag will differ.
|**Retrieval method**|Modern SCM| 
|**Source Code Management**|Git| 
|**Project Repository**|https://github.com/project-sunbird/sunbird-devops.git| 

22.Click **Save** and go to **Manage Jenkins** -> **Configure global security** 

23.Choose the **Markup Formatter** as **Safe HTML** and Under **Copy Artifact Compatibility mode**  choose **Migration**

24.Go to **Manage Jenkins** -> **Manager Nodes** -> Click **master** -> Click **Configure** -> Provide **labels**. Provide the label as `build-slave`, `jenkin-slave11`

25.Set the number of executors. For example, if your system configuration is: RAM - 16 GB and CPU - 4 core, set the number as 15. Adjust this number based on your system configuration 

26.Restart Jenkins using the command `sudo service jenkins restart` 

27.Switch back to the terminal session on the Jenkins server using the following commands:

    sudo su jenkins  
    mkdir -p /var/lib/jenkins/secrets && cd /var/lib/jenkins/secrets  
    touch deployer_ssh_key ops_ssh_key vault-pass
    chmod 400 deployer_ssh_key ops_ssh_key vault-pass 

28.The key used to login to the Jenkins server will henceforth be named `ops_ssh_key`. Example:

    ssh -i somekey.pem ubuntu@jenkins-server-ip
    Here `somekey.pem` is the key you used to login to the Jenkins server. Henceforth it will be named `ops_ssh_key` 

29.Copy the contents of the key that is used to connect to the VM into the `ops_ssh_key` file 

30.Create a new ssh key on local machine or on any server. This key will be used for a different user, for example,**deployer**. The name is as per your liking 

31.Use the command `ssh-keygen -f deployer_ssh_key`. The passphrase for this user should be empty

32.Copy the contents of the `deployer_ssh_key` into `/var/lib/jenkins/secrets/deployer_ssh_key`  

33.Copy the kubernetes config file `k8s.yaml` into `/var/lib/jenkins/secrets/k8s.yaml`

34.If your private GitHub repository has Ansible encrypted files, then enter the decryption password in `/var/lib/jenkins/secrets/vault-pass`. If there are no encrypted files, then enter a random value like **12345** into the `vault-pass` file. This file cannot be empty

35.give sudo access to jenkins user 
 ```
    sudo visudo
    addd below line in visudo
    jenkins ALL=(ALL) NOPASSWD:ALL   

 ``` 

36.Restart the Jenkins server


> Note: 
> Open any of the config files from the **Deploy** directory and save it. Without this, some of the parameters will not be visible 
> After completing the Jenkins setup, follow instructions provided in the Running Builds, Artifact Uploads and Deployment section to create inventory, secrets and Ansible hosts in the private repository        

## 6. Understanding Jenkins Scripts

## Overview
To effectively do the Jenkins setup, it is important to understand the various scripts, jobs, job parameters and variables involved. This page provides details of all these. 

## Script Details

The following scripts are used by Sunbird for the Jenkins setup.

| Script Name | Description |
|----------------|----------------|
| jenkins-server-setup.sh |This script installs Jenkins and other packages like Maven, Ansible, Pip, etc.|
| jenkins-plugins-setup.sh |This script downloads the m2 repo, if it does not exist and installs the plugins using Butler. The plugin list can be found in the plugins.txt file.|
| jenkins-jobs-setup.sh |This script takes the **envOrder.txt** file as the input and creates the jobs directory in **/var/lib/jenkins**. The jobs directory in **sunbird-devops/deploy/jenkins** is the base for this script, and uses it to create the folder structure.|

## Jenkins Setup Variables

The following variables are used for Jenkins setup.

|Variable Name| Description|
|-----------------|----------------|
|ops_ssh_key| The private key value used to create the VM. It is considered the master key used to can connect to the VM's.|
|deployer_ssh_key| The new key generated on a local machine or any other machine. Ansible creates a new user on all the VM's during the bootstrap process. The user name is found in the common.yml file. After this user is created, Ansible uses this key for all Jenkins jobs. The private key content is copied into this file during the Jenkins setup. The public key will be sprayed to all VM's during the bootstrap process.|
|vault-pass| The password to decrypt to files encrypted using Ansible-vault. <br>The best practice is to encrypt the **secrets.yml** file using Ansible-vault and push it to the private github repo. When Ansible runs, it checks out this file and decrypts it using the vault-pass file. <br>Even if the **secrets.yml** file is not encrypted, it is a must to enter a value in this file. If the file is empty, Ansible will throw an error even if there are no files to decrypt.|

## Jenkins Environment Variables

The following environment variables are used for Jenkins setup.

|Variable Name| Description|
|---------------|----------------|
|hub_org|This is your docker username, docker organisation name.|
|private_repo_branch| The branch name in your private repo where the Ansible hosts, common.yml and secrets.yml files exist.
|private_repo_url| The URL of your private repository. After the repository is created, the URL can be obtained from the browser URL or on clicking the **Clone or Download** button on GitHub. It display an https URL.|
|private_repo_credentials| The ID field value from Jenkins. After the username and password of the private repo are added in the Jenkins global credentials field, it displays an auto generated ID or the user can set a unique ID.|
|public_repo_branch|This is a unique variable. All the jobs in Jenkins are by default configured to checkout the Jenkins file from this variable as ${public_repo_branch}. For example, assign the value release-2.8.0 to this variable, the Jenkins jobs checkout the Jenkins file from release-2.8.0 branch, from the URL, configured in the job. If the value is refs/tags/release-2.8.0, then Jenkins checks out the file from the tag release-2.8.0. You can change this variable in the Jenkins job configurations and specify a branch or tag name. It is useful when you want to run some jobs from a different branch or tag instead of the value mentioned in the variable.|
|deploy-conf| The name of the library that is used in the Jenkinsfile. This file is used in the global pipeline libraries section. When the Jenkinsfile has this library name, it checks out a couple of common libraries from the URL configured in this section. The Jenkinsfile requires these libraries to run. To avoid writing the same code at multiple places, **common code** is placed in a separate branch and all the Jenkinsfile can use this common code by calling it as a function. If this name is changed, ensure the name in Jenkinsfile is also changed to the new library name|.
|override_public_branch|This variable is used to enable or disable the parameter private_branch in Jenkins jobs. If the value of this variable is true, it gives you an option to enter the private branch in case you want to use a different branch for specific jobs essentially overriding the global variable private_repo_branch. |
|override_private_branch|This variable is used to enable or disable the parameter branch_or_tag. If you set the value of this variable to true, it will give you an option to enter the tags for different repositories. This is useful when different repositories have different tags. Using this variable, you can override the global variable public_repo_branch.

## Job Parameters

### Build Jobs

|Parameter| Description|
|---------------|-----------|
|github_release_tag |Specify a tag name from which you want the build, example - release-2.8.0. It looks for a tag named release-2.8.0 in the repository URL configured in the Jenkins job and checks out the code from this tag. This tag is different from public_repo_branch which is used only to check out the Jenkinsfile which has all the build logic.|

> **Note:** Even if the **public_repo_branch** is configured to a tag name, you need to provide a tag name in this parameter box when running the build. If this is empty, it will checkout code from the tag specified in **public_repo_branch** but it will not tag build artifact with the tag name. Instead it will tag it with commit hash which is undesirable when you want to build from tag. All build jobs create an artifact **metadata.json** that has details such as artifact/docker image name and version, and the Jenkins slave on which it was built.

### Artifact Upload Jobs

|Parameter| Description|
|---------------|----------------|
|absolute_job_path| The path from where the **metadata.json** file is copied. The **metadata.json** file contains important information like name and version of artifact/docker image and the Jenkins slave on which it was built. It is not recommended to change this value. All jobs in Jenkins heavily rely on the **metadata.json** file to obtain the needed information and the path to this file is critical. For all ArtifactUpload jobs, the **metadata.json** file is copied from the build jobs.|
|Image_tag| This is available for docker container jobs. This field may have a value or may be blank. By default when the ArtifactUpload job runs, it copies the image name and image tag from the **metadata.json** file and pushes it to the container registry specified in **hub_org**. As an example scenario, let us see when we need to provide the value in this field. Let's say the build job X with build number 1 completed and triggers the ArtifactUpload job. For some reason, the ArtifactUpload job fails and it could not push the image built by the build job X in build number 1. The build job X runs again after some new commits and triggers the ArtifactUpload job. This time the upload jobs pushes the image to **hub_org**. But the image build in build number 1 is not available in the docker hub as the upload failed. In this scenario, go to the build number 1 of job X and copy the image tag from metadata.json file. Paste the image tag value in this parameter of the upload job. This will push that specific version of the image to **hub_org**.|
|build_number|This is same as the **image_tag** of docker builds, except that it is used for other type of jobs where zip / tar / jar type artifacts are created. This field is optional and may be blank. Apply the same scenario explained above to this field in order to understand the usage of this field. Since these are artifacts and not containers, you need to use the build job number to copy the artifact. Every new run of any job will clear the workspace but the artifacts are archived on the Jenkins master.|
|artifact_source| For docker jobs, this parameter is default as **ArtifactRepo** and cannot be changed. All containers must be pushed to some hub so that it can be pulled from the hub during deployment.|

> **Note:** For other job types, you can choose to push the artifacts either to the Azure blob or store it in Jenkins. Pushing the jobs to the Azure blob also stores a copy in Jenkins but not vice versa. To push to Azure blob, ensure you have set up your Ansible hosts and **common.yml** and **secrets.yml** files. The default behaviour is to store in Jenkins job only.

### Deploy Jobs

| Parameter|  Description |
|---------------|----------------|
|artifact_source| In deploy jobs, the artifact is downloaded or pulled from the option specified. This is the opposite of the behaviour in ArtifactUpload jobs.|
|artifact_version|If you leave this value empty, by default it will take the version specified in the **metadata.json** file and deploy that version. In case you want to deploy some other version, you can provide the version value here.This is useful when you want to roll back to a previous version from current version.|
|private_branch|In this parameter you can specify the private branch. Specifying a value in this parameter allows you to override the global value set in the global variable **private_repo_branch**, thus allowing you to check out the inventory and variables from the branch specified here. By default it shows the value which is in the global variable **private_repo_branch**. 
|branch_or_tag|In this parameter you can specify the branch or tag of a repository. The public code like Ansible playbooks will be checked out from this branch or tag. Since each repository can have a different tag or branch, you need to specify this value.

### Summary Jobs

Every deploy folder has a summary job. This job consists of a **summary.txt** file that has details of all the versions currently deployed. You can use this file to see which version of the artifact is currently deployed in that environment.

### Artifact Pushes

1.The default configuration uploads the artifacts (zip, jar, etc files) to Azure blob and the docker containers to the configured container registry.
2.It is mandatory to have a hub account to push the docker container.
3. If you would not like to use the Azure storage to store artifacts, like zip files which get generated after a build, you can change the jenkins job configuration to not use Azure. To do this, go to the Jenkins ArtifactUpload jobs and change the order from [ArtifactRepo, JenkinsJob] to [JenkinsJob, ArtifactRepo].

## Log Rotation
   
By default, the Jenkins configuration for log rotation for build jobs is set to 1 and for all other builds it is set to 5. However, you can change this value under **job configuration** -> **Discard old builds** -> **Advanced** -> **Max # of build artifacts to keep**.

> **Note**: When the **jenkins-jobs-setup.sh** script is triggered, it overwrites these changes. You can run a simple find and replace using any editor to make the configuration changes as per your requirement. The find and replace needs to be run on the **config.xml** files. This can be done even before the **jenkins-jobs** script runs or later in the **/var/lib/jenkins/jobs** directory.
> Ensure you take a backup.


## 7. Running Builds, Artifact Uploads and Deployments


## Overview

After completing the Jenkins setup, you need to build and deploy Sunbird services to bring up the Sunbird instance. Sunbird services are deployed through build jobs. Most of the build jobs create artifacts either as zip files or docker images.

> **Note**:To understand the scripts, jobs, job parameters and the associated environment variables, refer to the Understanding Jenkins Scripts Section 

Adhere to the following sequence to create the build:

1. Knowledge Platform
2. DataPipeline
3. Plugins
4. Core Services


Adhere to the following sequence to Provisioning the infra:

1. Knowledge Platform
2. DataPipeline
3. Plugins
4. Core Services



Adhere to the following sequence to deploy the services: 

1. Knowledge Platform
2. DataPipeline
3. Plugins
4. Core Services

Do find the below stracture for the same:

<img src='images/deployment_complete-sheet_all.png'>

## 8.  Artifact Upload Jobs


## Overview
An artifact is a deployable component of your application. Originally, they were called **Build Artifacts**, but as more processes were applied other than build to create them, they are now simply referred to as artifacts. Artifacts can be recreated from the code repository using the same process, if you have preserved the environment in which the process was applied. However, this process is time consuming and the environment may be imperfectly preserved. To be able to recreate artifacts in the exact same way, they are now stored in Artifact Repositories. 
When creating an instance of Sunbird, you need to link the appropriate artifact sources to your Sunbird release pipeline. 

## Artifact Upload Job Information

- Most build jobs create artifacts either as zipped files or as docker images 
- These artifacts are uploaded to the container registry as docker images or Azure blobs as zip files  
- Create artifact versions with the value provided in **github_release_tag**
- A drop-down parameter, **ArtifactSource**, is associated with every artifact upload job. This parameter has two valid values **ArtifactRepo** (default) and **JenkinsJob**. You can choose either
- If you choose **ArtifactRepo**, a zip file is uploaded to Azure blob and docker images are uploaded to the container registry
- The Jenkins job also maintains a local copy of the zip files as an artifact
- All docker images are pushed to the Docker Hub. This is mandatory
- You may choose not to upload the zip file to Azure blobs. To do so, either edit the Jenkins config files to use only the default option **JenkinsJob** or choose **JenkinsJob** and re-run the build

> **Note:** 
> Having a copy stored in the Azure blob, gives you the option to rollback a version quicky. You can specify the version that you want to use, rather than rebuilding the service and then deploying it.
> All artifact upload jobs have a log rotation of 5. This means the last 5 artifcats are stored in Jenkins as a local copy. You can modify this number, if required. Doing so makes your local Jenkins your artifact store.
                    

## 9. Knowledge Platform


## Overview
This page explains the jobs to be run to bring up the Knowledge Platform services. In order to do so, log into Jenkins and execute the instructions as per the sequence provided on this page.

## Build

Switch to the `Build` folder and run all jobs in the below sequence. For the value of the **github_release_tag**, refer to Current Release Tags and Jenkins Jobs Reference


|Operation Name    | Function              | CURRENT RELEASE TAG |
| --------------   | --------------------- | -------------------
| CassandraTrigger | Generate the jar file for cassandraTrigger | release-3.4.0
| KnowledgePlatform| Generate the artefacts for knowledge Platform | release-3.4.0
| Neo4j            | Generate the Neo4j jar file| release-3.4.0
| Yarn             | Generate the artefacts for Samza job | release-3.4.0
| Dial             | Generate the artifacts for dial service | release-3.3.0
| FlinkJobs        | Generate the artifacts for flink jobs   | relelase-3.4.0




For KnowledgePlatform build, use the default profile_id which is "platform_services".

## DevOps Administration

| Operation Name | Function              |
| -------------- | --------------------- |
| Bootstrap      | Creates Deployer User |

## Provision

*   Download **neo4j enterprise** version 3.3.x. The file should be uploaded to your azure storage account under the root path. The file name should be **neo4j*.tar.gz**. This file should be uploaded to the container named in the variable {{artifacts_container}} of KnowledgePlatform's common.yml.

The URL for this path will look like this - https://{{storage_account_name}}.blob.core.windows.net/{{container_name}}/neo4j-enterprise-3.3.9-unix.tar.gz

*   Switch to `Provision/<env>/KnowledgePlatform` and run the jobs in the following sequence:   
    
| Operation Name | Function              | Description | CURRENT RELEASE TAG |
| -------------- | --------------------- |-------------|  -----------------
| CompositeSearch| Installs the ElasticSearch | Installs dependencies for elastic search and starts elastic search service | release-3.4.0
| Neo4j          | Installs the Neo4j and Logstash | Installs Neo4j and its dependencies | relesae-3.4.0|
|zookeeper          | Installs the zookeper | zookeeper installation | release-3.4.0
| Kafka          | Installs the Kafka | Kafka installation | release-3.4.0
| Learning       | Installs the dependency to run the learning application | Creates learning user and installs dependencies(java, tomcat, logstash, ffmpeg,imagemagick) | release-3.4.0
| Redis          | Installs the Redis database | Download and installs Redis | release-3.4.0
| Dial           | Installs the dependency to run the dial service | install logstash and other dependencies | release-3.4.0

## Deploy

*   Switch to `Deploy/dev/KnowledgePlatform` and run the jobs in the following sequence (start deploy jobs after all DB provision is done from core folder):
 
| Operation Name      | Function              | CURRENT RELEASE TAG |
| --------------      | --------------------- | -------------------
| CassandraTrigger    | Deploys the CassandraTrigger jar file and installs the logstash | release-3.4.0
| CassandraDbUpdate   | Creates the Cassandra keyspace and update| release-3.4.0
| Neo4j               | Deploys the Neo4j artefacts | release-3.4.0
| StartNeo4jCluster   | Starts the Neo4j process| release-3.4.0
| Learning            | Deploys the learning service artefacts and starts the learning service | release-3.4.0
| Neo4DefinitionUpdate| Updates Neo4j definition | release-3.4.0
| KafkaSetup          | Creates the Kafka Topics| release-3.4.0
| Yarn                | Deploys the Samza jobs | release-3.4.0
| Dial                | Deploys and starts dial service | release-3.4.0
| FlinkJobs           | Deploys flink jobs | release-3.4.0


## Create master framework category
        
1.Make sure learning services is running and healthy, command to check learning service health **http://localhost:8080/learning-service/health**

2.Run the below curl command from learning vm:

 ```
   curl --location --request POST 'http://localhost:8080/learning-service/framework/v3/category/master/create' \
                  --header 'Content-Type: application/json' \
                  --data-raw '{
                     "request": {
                        "category":{
                            "name":"Subject",
                            "description":"Subject",
                            "code":"subject"
                        }
                      }
                  }'

 ```   

Refer How to Create Framework section  and How to Create Schemas for Knowledge Platform Objects section

## 10. Data Pipeline


## Overview

This page explains the jobs to be run to bring up the Data Pipeline services. In order to do so, log into Jenkins and execute the instructions as per the sequence provided on this page.

## Build

Switch to the `Build` folder and run all jobs. For the value of the **github_release_tag**, refer to Current Release Tags and Jenkins Jobs Reference


| Operation Name | Function  |Description | CURRENT RELEASE TAG |
| -------------- | --------- |------------|---------------------|
| AnalyticsCore  | Builds the analytics framework|Creates analytics framework artifact|release-3.4.0
| Core Data Product| Builds analytics core data products| Creates analytics core data products artifact| release-3.4.0
|EdDataProducts| ---|---| release-3.4.0|
|FlinkPipelineJobs| ---|---| release-3.4.0|
|ETLJobs | Builds adhoc scripts which includes content snapshot druid indexer, dialcode redis indexer| Creates jar out of adhoc scripts| release-3.4.0|


## DevOps Administration

| Operation Name | Function              |
| -------------- | --------------------- |
| Bootstrap      | Creates Deployer User |

## Provision

Switch to `Provision/<env>/DataPipeline` and run the jobs in the following sequence: 


| Operation Name | Function              | Description| CURRENT RELEASE TAG|	
| -------------- | --------------------- |-------------|------------------|
| AnalyticsSpark | Provision the server to run the data products jobs| Creates the analyticsuser required folders and installs the dependencies to run spark| release-3.4.0
| Postgres       | Provision the Postgres database| Installs the dependencies like java and install the Postgres database| release-3.4.0
| postgres-managed          | --- | ---| release-3.4.0|
| Yarn           | Provision the Yarn cluster | Install the dependencies required to create the yarn cluster and creates the yarn cluster to run samza jobs| release-3.4.0
| Influxdb       | Provision the influxdb database| Install the dependencies like java and install the influxdb database| release-3.4.0
| Druid          | deploy and start druid services


## Deploy

Switch to `Deploy/<env>/DataPipeline` and run the jobs in the following sequence:


| Operation Name | Function              | Description | CURRENT RELEASE TAG|
| -------------- | --------------------- |-------------- |------------------|
| AnalyticsCore | Deploys Analytics framework jars |Copies analytics framework artifact to spark server| release-3.4.0|
| CoreDataProducts | Deploys core data products  |Copies core data products artifact to spark server| release-3.4.0|
| EdDataProducts | ---  |---| release-3.4.0|
| KafkaIndexer | Deploys logstash configurations and starts process  |Used for pushing pipeline metrics to influxDB| release-3.4.0|
| KafkaSetup | Setup kafka for pipeline  |Creates kafka topic and updates topic configurations| release-3.4.0|
| FlinkPipelineJobs | --- | --- | release-3.4.0 |
| DruidIngestion | --- | --- | release-3.4.0 |
| ETLJobs | --- | --- | release-3.4.0 |
| ETLUserCacheIndexer | ---  |---| release-3.4.0|



## 11. Core Services

## Overview
This page explains the jobs to be run to bring up the Core services. 

## Prerequisites

1.Create a container in the Azure blob and make it public to publish content. The container name must be the same as the variable **sunbird_content_azure_storage_container** specified in the **common.yml** file present in the core inventory.

2.Switch to the `Build` folder and run all jobs. Set the value for **github_release_tag** according to the details mentioned in this page - Current Release Tags and Jenkins Jobs Reference

## Running the Jobs
    
> Note: Adhere to the following sequence when running the jobs.

### DevOps Administration

| Operation Name | Function | 
|--------------------|-----------| 
| Bootstrap | Creates Deployer User | 

### Builds

| Build Name | Function (Builds) | CURRENT RELEASE TAG |
|--------------------|-----------| ------------------- 
| Adminutils | Adminutils docker image | release-3.4.0
| API MANAGER | API manager docker image | release-3.4.0
| API MANAGER Echo | API manager echo docker image | master
| Badger | Badger docker image |
| Cassandra | Jar for migration| release-3.4.0 |
| Lms | LMS service docker image | release-3.4.0
| Content | Content service docker image | release-3.4.0
| Search  | search service docker image |release-3.4.0 
| KnowledgeMW | Knowledge-mw service docker image | release-3.4.0
| Learner | Learner service docker image | release-3.4.0
| Player | Player service docker image. | release-3.4.5
| Cert | Cert service docker image | release-1.1.1
| EncService | Envservice docker image | release-1.1.1
| Proxy | Proxy docker image | release-3.4.0
| Telemetry | Telemetry docker image | release-2.1.0
| Keycloak | Keycloak zip file | release-1.15_RC9|
| Analytics | Analytics service docker image | release-3.4.0
| Taxonomy  | Taxonomy service docker image | release-3.4.0

### Artifacts  
Ensure that all Artifacts are uploaded

### Provision

| Operation Name | Function | Description | CURRENT RELEASE TAG |
|--------------------|-----------|---------| ------------------- |
| (Deploy) ApplicationES ( Kubernetes folder) |  Provisions Elasticsearch 6.2.3 version used by core services | From the Deploy Folder, **Deploy ApplicationES** provisions for the Elasticsearch and creates indices necessary for Sunbird Core| release-3.4.0
| ESMapping (Under OpsAdministarion. Provide the value as *all* for job parameter indices_name) | Creates Elasticsearch indexes | Creates the specific index with its mapping or all indices which will be used by our sunbird app | release-3.4.0
| Postgres | Provisions for Postgres | Provisions the Postgres instance | release-3.4.0
| PostgresDbUpdate | Creates the databases, assigns roles and creates users | Creates a schema with DB's, Tables | release-3.4.0 **Perform Kong migration once db update is done, steps are mentioned in the kong migration section**
| EsReindexing (Under OpsAdministario, parameters should be esHost: es ip, oldIndex: certreg, newIndex: certv2, aliasName: certs, indexFileLocation: sunbird-es-utils/src/main/resources/reindexing/indices/certreg_indices.json, mappingFileLocation: sunbird-es-utils/src/main/resources/reindexing/mappings/certreg_mappings.json) | --- | --- | release-3.4.0

### Deploy

| Operation Name | Function | Description |  CURRENT RELEASE TAG |
|------|------|---------------------------------| --------------|
|BootstrapMinimal |creating the namespace for kubernetes orchestration and also Bootstrap minimal create docker registry secrets and nginx ssls also | Creates namespace for the core service | release-3.4.0 
|nginx-private-ingress |xxxx | xxxx | release-3.4.0
| API Manager | Deploys the API manager Kong and API manager Echo | Manages consumers and APIs of sunbird | release-3.4.0
| OnboardAPIS | Onboards all API's to Sunbird | onboards sunbird API's | release-3.4.0
| OnboardConsumers | Onboards sunbird consumers |onboards sunbird consumers | release-3.4.0
Update **core_vault_sunbird_api_auth_token**, **core_vault_kong__test_jwt** and **core_vault_sunbird_ekstep_api_key** with the **jwt token** from the Jenkins output of **api-management-test-user** if you are using the Knowledge Platform and Data Pipeline along with core| Generates user specific key |  Onboards new consumer to Sunbird and generates the consumer specific API key | 
| (Provision) Cassandra |Provisions Cassandra and create keyspaces required for Sunbird Core | Provisions Cassandra and creates keyspaces and performs migration | release-3.4.0|
| Cassandra | Performs keyspace schema migration | Performs migration if required. Deploy this thrice by choosing different zip files using the build_number parameter. Ensure that you get a success message for the Cassandra migration on the Jenkins console output. Do not rely only on the red or green status indicator on Jenkins job | release-3.4.0
|(Provision) Keycloak | Install dependencies for keycloak | Provisions Keycloak by installing prerequisites like Java and environment variables | release-3.4.0
| Keycloak | Deploys Keycloak service to VM | Centralized user management for sunbird | release-3.4.0
| KeycloakRealm(Core folder) |  User management -Creates sunbird realm | Creates a Sunbird Realm. After the Sunbird realm is created, configure Keycloak by using the steps mentioned in the **Keycloak Configuration** section | release-3.4.0
|Adminutil | Deploys the Adminutil container | Creates tokens for the sunbird devices | release-3.0.0
| Player |  UI for sunbird  | Deploys portal UI | release-3.4.0
| Learner | Deploys the Learner Service |  Handles user management and helps to search org | release-3.4.0 **before deploying learner service Create root org by using the steps mentioned in the Create Org section below**
| Content | Deploys content service | Helps to create content | release-3.4.0
| Search              | Deploys the search service artefacts and starts the search service | |release-3.4.0
| KnowledgeMW | Deploys knowledgemw service | Deploys the knowledgemw service | release-3.4.0
| Lms | Deploys LMS Service | Provides the APIs for LMS functionality of Sunbird| release-3.4.0
| EncService | Deploys enc service | Encrypts and decrypt the keys to generate certificate | release-3.4.0
| Cert | Deploys cert services | Issues certificates | release-3.1.0
| Telemetry | Aggregates and send telemetry data to kafka | Telemetry management service| release-3.4.0
| Analytics | Deploys Analytics service | Deploys Analytics service | release-3.4.0
| Taxonomy | Deploys Taxonomy service | Deploys Taxonomy service | release-3.4.0 after deploying taxonomy service create master category and run definition scripts by using the steps mentioned in **Create master category** section
|nginx-public-ingress | Deploys Proxy service | Deploys Nginx | release-3.4.0 
|BootstrapMinimal |creating the namespace for kubernetes orchestration  and also Bootstrap minimal create docker registry secrets and nginx ssls also| Creates namespace for the core service | release-3.4.0

### Keycloak Configuration 

> Note: From release-2.0.0 keycloak admin portal is disabled from public internet. 
You must tunnel the port in to the local machine via ssh tunnelling.  
`ssh -L 8080:localhost:8080 ops@~keycloak-ip-address~`  
You can access keycloak via `localhost:8080`

| Step | Action  |
|------|----------------------------------------------------------------------------------------------------------------------------------------------------|
| 1 | Login to Keycloak using the user name **admin** and password as given in the private **secrets.yml** file. Or, login to keycloak using &#60;localhost:8080&#62;/auth |
| 2 | Navigate to Sunbird Realm > Realm Settings > Keys. Click `Public Key`. Copy the key value that you see and update the variable core_vault_sso_public_key |
| 3 | Creating keycloak federation [Deployment Steps for Keycloak User Federation](developer-docs/server-installation/keycloak_user_federation) |
| 4 | Creating keycloak lms client [Creation Steps for Keycloak lms client](developer-docs/server-installation/keycloak_lms_configuration) |
| 5 | Create Keycloak Password policy: navigate to Authentication > Password Policy > from add policy drop down select `Hashing Iterations, Minimum Length, Special Characters, Uppercase Characters, Lowercase Characters`
| 6 | get the value for variable adminutil_refresh_token_public_key_kid [STeps to get the value for variable](developer-docs/server-installation/adminutil_refresh_token) |


**Note:**
If the Cassandra migration fails, run the query manually to set the corresponding version for the failed migration to True

**Example:**

`SELECT * from sunbird.cassandra_migration_version;`

Check the rows for which the value in the success column is False. The following is an example -

`1.80 |   180685665 |   cassandra |              4 |         null | 2019-12-01 13:58:52.401000+0000 |            136 | V1.80_cassandra.cql |   False |  CQL |           80`

Run the update query for each row separately 

`update sunbird.cassandra_migration_version set success=True where version ='1.80';`

Verify that all the values in the success column are True and rerun the Jenkins job again with same zip file and tag

Once this succeeds, use the second zip file and tag to deploy again

The current migration version is 1.88. The output of the Jenkins job should be as follows -

`Migrating keyspace Sunbird to version 1.88 - Cassandra
Successfully applied 3 migrations to keyspace sunbird (execution time 00:20.547s).
Migration Completed at ==1571996508540`


### Create Root org

```
   # Create root org
      # cqlsh
      INSERT INTO sunbird.organisation (id,channel,orgname,hashtagid,isrootorg) values ('1234567890','default', 'default','1234567890',true);
      
      # from Elastic search
      # Sync to es
      curl -X PUT "localhost:9200/org/_doc/1234567890?pretty" -H 'Content-Type: application/json' -d'
      {
        "id": "1234567890",
        "channel": "default",
        "orgname": "default",
        "hashtagid": "1234567890",
        "isrootorg": true
      }
      
      #cqlsh
      # Insert in to system settings
      INSERT INTO sunbird.system_settings (id,field,value) values ('custodianOrgId','custodianOrgId','1234567890');
      INSERT INTO sunbird.system_settings (id,field,value) values ('custodianOrgChannel','custodianOrgChannel','1234567890');

```

### Kong migration

```
   set or export postgres variables
   export PG_HOST="postgres ip"         
   export PG_USER="postgres"                    
   export PGPASSWORD="pgpassword"   #  copy value from core_vault_postgres_password variable       
   export PG_DB="kong db name"   # by default db name will be api_manager_{{env}}, verify in postgres database for knong db name              
   
   docker run --name kong-migration \
   -e ""KONG_DATABASE=postgres"" \
   -e ""KONG_PG_DATABASE=$PG_DB"" \
   -e ""KONG_PG_HOST=$PG_HOST"" \
   -e ""KONG_PG_PASSWORD=$PGPASSWORD"" \
   -e ""KONG_PG_USER=$PG_USER"" \
   -e ""KONG_PROXY_ACCESS_LOG=/dev/stdout"" \
   -e ""KONG_ADMIN_ACCESS_LOG=/dev/stdout"" \
   -e ""KONG_PROXY_ERROR_LOG=/dev/stderr"" \
   -e ""KONG_ADMIN_ERROR_LOG=/dev/stderr"" \
   -e ""KONG_ADMIN_LISTEN=0.0.0.0:8001"" \
   -p 8000:8000 \
   -p 8001:8001 \
   kong:0.14.1 kong migrations up --vv

```

### Create master category

1.login to taxonomy service container and execute the curl commands to create master category. curl commands are availabe in the file [master_category_create](https://github.com/project-sunbird/knowledge-platform/blob/release-3.4.0/definition-scripts/master_category_create) 

2.Replace the host to http://localhost:9000 while running

3.Run other defintion scripts from taxonomy service [definition-scripts](https://github.com/project-sunbird/knowledge-platform/tree/release-3.4.0/definition-scripts) Since master_category_create is alredy ran do not run again.


## 12. Plugins


## Overview
This page explains the jobs to be run to build and deploy plugins. In order to do so, log into Jenkins and execute the instructions as per the sequence provided on this page.

## Build

Switch to the `Build/Plugins` folder and run all jobs. Ensure all ArtifactUpload jobs are sucessful after the build. For the value of the **github_release_tag**, refer to [Current Release Tags and Jenkins Jobs Reference](developer-docs/server-installation/current_release_tags_n_jenkins_jobs){:target="_blank"}

### Prerequisites Before Deployment

**Create Containers**

Create containers in Azure blob. Use the following commands to create initial placeholder containers:

```
az storage blob upload-batch --destination container_name/collection-editor --source some_folder --account-name storage_account_name --account-key account_key

az storage blob upload-batch --destination container_name/generic-editor --source some_folder --account-name storage_account_name --account-key account_key

az storage blob upload-batch --destination container_name/content-editor --source some_folder --account-name storage_account_name --account-key account_key

az storage blob upload-batch --destination container_name/v3/preview --source some_folder --account-name storage_account_name --account-key account_key
```

> **Note:**
1. The commands only create the required directories in Azure blob. The deployment script always tries to delete the folders before deploying new contents. Hence, if these folders are not available during the first run, the deployment script fails.
2. You can upload any content in the blob for the first run. Azure does not allow you to create emtpy folders. Hence, add a file in a folder named *dummy*. The commands mentioned create the required directories.
3. The **container_name** container must be publicly accessible.


**Upload Base Plugin**

Upload the initial set of plugins (base plugins) to your publicly accessible Azure container. 
> **Note:** The name of the Azure container must be same as the **container_name** mentioned in the section on creating containers.

You can download the initial Plugins from [here](https://sunbirdpublic.blob.core.windows.net/installation/content-plugins.zip)

Unzip the contents and the directory **content-plugins** is available. Run the following command from the directory where the zip was extracted.

```
az storage blob upload-batch --destination container_name/content-plugins --source content-plugins --account-name account_name --account-key account_key
```

## Deploy

Switch to `Deploy/<env>/Plugins` in your jenkins machine and run the jobs in the following sequence:

1.ContentPlugins  
2.ContentPlayer  
3.CollectionEditor  
4.ContentEditor  
5.GenericEditor  


## 13. Jenkins Tweaks

## Overview

This page explains how to create and remove folders for Jenkins Jobs

## Creating Folders for Jenkins Jobs 

The process of creating folders for Jenkins jobs is best explained using a scenario. 
Consider the scenario in which your current infrastructure consists of two environments - **dev** and **production**. In this scenario, the **envOrder.txt** file has an entry: 

```
dev=0
production=1
```

The general jobs workflow is **Build -> ArtifactUpload -> Deploy** 
The **Build** job automatically triggers the **ArtifactUpload** job which in turn automatically triggers the **Deploy** job. This workflow is executed only for the first environment, which in the example scenario is, the **dev** environment.  
The **production** environment has deploy jobs that pick up artifacts from the **Deploy** directory of the **dev** environment. 
The workflow remains the same irrespective of the number of directories in an environment.  The nth directory picks up artifacts from the n -1 directory. In case of the first directory, (in the example scenario, the dev directory) the deploy jobs pick up artifacts from ArtifactUpload job. 

Now, consider a new scenario in which there is an added environment named **pre-production**.  To add this environment and create jobs for this environment, do the following steps:

1.Update the **envOrder.txt** file and re-run the **jenkins-jobs-setup.sh** script.  

2.Hence, consider that the **envOrder.txt** file is updated as: 

        dev=0
        pre-production=1 
        **production=2

3.On executing the  **jenkins-jobs-setup.sh** script, it first displays the old, existing folder structure and checks for confirmation on its correctness. 

4.Since a new environment is getting added, specify the option as *n* in the confirmation dialogue box. 

5.Update the **envOrder.txt**.  (This is already done in the previous steps) 

6.Re-run the script and it displays the updated environment order that is added in the **envOrder.txt** file. 

7.Specify *y* in the script's confirmation dialogue box and restart Jenkins after executing the script. 

8.You can now see the new directory and jobs on Jenkins. 

9.In this scenario, the production jobs are configured to pick artifacts from the pre-production directory, and pre-production jobs are configured to pick artifacts from the dev environment.    
Repeat the steps to add any new environment and create jobs in that environment.
 
## Removing Folders for Jenkins Jobs  

To purge an environment or if you no longer require Jenkins jobs for the environment, the process to update Jenkins jobs is quite simple. Follow the same steps as mentioned in **Creating New Folders for Jenkins Jobs** section. However, instead of adding a new entry, remove the environment that is not required from the **envOrder.txt** file and update the order number (0,1,2 etc) 

Keeping the same scenario as in the **Creating New Folders for Jenkins Jobs** section:  

**Before Purge** the file entry is as follows:


    dev=0 
    pre-production=1 
    production=2

**After Purge** the file entry is as follows


    dev=0
    production=1


1.Run the **jenkins-jobs-setup.sh** script as mentioned in above steps, this updates the job configuration accordingly.  
2.You can also manually remove the **pre-production** environment from the Jenkins UI. 

Repeat the same steps to remove any environment.              


## 14. Keycloak User Federation

## Overview

After you create or remove folders for jenkins job, create a Keycloak User Federation.
 

## Deployment Steps for Keycloak User Federation

To create a Keycloak user federation, ensure to take a backup of the Keycloak database and then complete the following steps:

1.Log in to the admin console using admin account

2.Click the User Federation tab in the left navigation pane as shown here. The User Federation page opens

<img src='images/keycloak_user_federation.png'>

2.Select the cassandra-storage-provider option from the Add provider drop-down list and the **Add user storage provider** page is displayed 

<img src='images/keycloak_user_storage_provider.png'>

3.Click the **Save** button to generate a **Provider ID** 

<img src='images/keycloak_cassandra_storage_provider.png'>

4.Copy the **provider ID** 
5.Update the private repository inventory under **Core/secrets.yml** for the variable.   
    `core_vault_sunbird_keycloak_user_federation_provider_id`



## 15. Post Installation Configurations



## Overview

This page provides details on the configuration to be done, after you complete installation of Sunbird. The configurations mentioned in this page are the basic requirements to do any other configuration as mentioned in the **Sever Configuration** section. Execute the configurations in the sequence mentioned on the page.    


## Create User Access Token

To create a user access token use the following cURL command: 

   
      `curl -X POST {dns_name}/auth/realms/sunbird/protocol/openid-connect/token \
      -H 'cache-control: no-cache' \
      -H 'content-type: application/x-www-form-urlencoded' \
      -d 'client_id=admin-cli&username=user-manager&password={password}&grant_type=password'

      `
Replace the values within { } braces with your environment values.

| **Variable Name**| **Description**|
|------------------|----------------|
|{dns_name} |Domain or the IP address of your application server installation|
|{password} |Password of the `user-manager` user. The password is same as the value of the `sso_password` parameter in the Config file|

## Create root organization
To create a root organization use the following cURL commands: 

      `curl -X POST  \
      {dns_name}/api/org/v1/create \
      -H 'Cache-Control: no-cache' \
      -H 'Content-Type: application/json' \
      -H 'accept: application/json' \
      -H 'authorization: Bearer {jwt token from ~/jwt_token_player.txt}' \
      -H 'x-authenticated-user-token: {access token created last step}' \
      -d '{
      "request":{
      "orgName": "{Your Organization Name}",
      "description": "{Your organization description}",
      "isRootOrg":true,
      "channel":"{Your Channel Name}"
      }
      }'`

Replace the values within { } braces with your environment values.


**Create License and update Channel default License set and Content License migration based on channel**

1.Run the following script on the swarm manager node VM to create License .
 
```
Copy below code and save file name as create_license
#!/bin/bash
echo "License name: $1"
echo "License descripition: $2"
echo "License url: $3"
curl -X POST \
  http://localhost:9002/license/v3/create \
  -H 'Content-Type: application/json' \
  -d '{
    "request":{
        "license":{
            "name": "'$1'",
            "description": "'$2'",
            "url": "'$3'"
        }
    }
}'

Run these commands
bash -x create_license CC\ BY-NC-SA\ 4.0 This\ license\ is\ Creative\ Commons\ Attribution-NonCommercial-ShareAlike https://creativecommons.org/licenses/by-nc-sa/4.0/legalcode
bash -x create_license CC\ BY-NC\ 4.0 This\ license\ is\ Creative\ Commons\ Attribution-NonCommercial  https://creativecommons.org/licenses/by-nc/4.0/legalcode
bash -x create_license CC\ BY-SA\ 4.0 This\ license\ is\ Creative\ Commons\ Attribution-ShareAlike https://creativecommons.org/licenses/by-sa/4.0/legalcode
bash -x create_license CC\ BY\ 4.0 This\ is\ the\ standard\ license\ of\ any\ Youtube\ content https://creativecommons.org/licenses/by/4.0/legalcode
bash -x create_license Standard\ YouTube\ License This\ license\ is\ Creative\ Commons\ Attribution-NonCommercial-ShareAlike https://www.youtube.com/
```

2.Run the above script from learning server to update the Channel-default license .  

```
Copy below code and save file name as channel_license.sh
#!/bin/bash
echo "Channel Id: $1"
echo "Default License: $2"
IFS=,
curl -X PATCH \
"http://localhost:8080/channel/v3/update/"$1 \
-H 'Content-Type: application/json' \
-d '{
   "request": {
      "channel": {
        "defaultLicense":"'$2'"
      }
    }
}'

Run the Command
./channel_license.sh <channle_Id> <license_name>

license_name can be choosen from the list which was created in the 1st Step.
```

3.Update content with channel specific default license, Run the below query in neo4j

```
match (n:domain{}) WHERE n.IL_FUNC_OBJECT_TYPE IN ["Content", "ContentImage"] AND n.channel="<channel id>" AND n.license<>"Standard YouTube License" SET n.license="<channel defaultLicense>";
```
