def getHost(){
    def remote = [:]
    remote.name = 'Mark1'
    remote.host = '10.0.0.11'
    remote.user = 'nathan'
    remote.port = 22
    remote.password = '980227'
    remote.allowAnyHosts = true
    return remote
}
pipeline {
    agent any
    environment {
        _version = sh(script: "echo `date '+%Y%m%d%H%M%S'`", returnStdout: true).trim()
        _repository="https://github.com/NathanDai/haetae.git"
        _branch_name="dev4/docker"
    }

    stages {
        stage('Git clone') {
            steps {
                echo "<<< Starting fetch code from git:${_repository}"
                 git url: "${_repository}", branch: "$_branch_name"
            }
        }

        stage('Build code') {
            steps {
                echo "<<< Starting build code"
                withMaven (
                    jdk: 'jdk1.8',
                    maven: 'maven3'
                    ){
                        sh 'mvn clean package -Dmaven.test.skip=true'
                    }
            }
        }

         stage('Upload aliyun oss') {
            steps {
                echo "<<< Upload aliyun oss"
                aliyunOSSUpload accessKeyId: 'LTAI4GC4LgwAtf15jKg6vhjU',
                accessKeySecret: 'YJQrW6PSJvFiktEFDMogp73UVYF1kj',
                bucketName: 'dashwood',
                endpoint: 'oss-cn-hangzhou.aliyuncs.com',
                localPath: '/web/target/haetae-web-0.0.1-SNAPSHOT.jar',
                maxRetries: '3',
                remotePath: "/Jenkins/haetae/"+"${_version}/"+"haetae-web-0.0.1-SNAPSHOT.jar"
            }
         }
         stage('Back up by remote ssh'){
             steps{
                 echo "<<< Back up by remote ssh"
                 sshCommand remote:getHost() ,command: "./sh/haetaeBak.sh"
             }
         }
         stage('Send jar by ssh'){
             steps{
                echo "<<< Send jar by ssh"
                sshPublisher(publishers: [sshPublisherDesc(configName: 'Mark1', transfers: [sshTransfer(cleanRemote: false, excludes: '', execCommand: 'ls', execTimeout: 120000, flatten: false, makeEmptyDirs: false, noDefaultExcludes: false, patternSeparator: '[, ]+', remoteDirectory: '/java/haetae', remoteDirectorySDF: false, removePrefix: 'web/target', sourceFiles: 'web/target/haetae-web-0.0.1-SNAPSHOT.jar')], usePromotionTimestamp: false, useWorkspaceInPromotion: false, verbose: false)])
             }
         }
          stage('Stop by remote ssh'){
             steps{
                 echo "<<< Stop by remote ssh"
                 sshCommand remote:getHost() ,command: "./sh/haetaeStop.sh"
             }
         }

         stage('Restart by remote ssh'){
             steps{
                 echo "<<< Restart by remote ssh"
                 sshCommand remote:getHost() ,command: "./sh/haetaeRestart.sh"
             }
         }
    }
}
