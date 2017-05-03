#!groovy

def purge() {
    sh 'rm -rf ~/.m2/repository/uk/co/deliverymind/'
}

def setReleaseVersion() {
    sh "(cd plugin; mvn versions:set -DnewVersion=${RELEASE_VERSION})"
    sh "git add -A; git commit -m 'Release version bump'"
}

def install() {
    sh "(cd plugin; mvn clean install)"
}

def runCoreITs() {
    sh "(cd plugin-it; mvn clean verify)"
    sh "mvn -pl plugin-it clean verify"
}

def runExtITs() {
    sh "(cd plugin-ext-it; mvn clean verify)"
    sh "mvn -pl plugin-ext-it clean verify"
}

def tagRelease() {
    sh "git tag ${RELEASE_VERSION}"
}

def release() {
    sh "(cd plugin; mvn clean deploy -P release -Dgpg.passphrase=${GPG_PASSPHRASE})"
}

def setSnapshotVersion() {
    sh "(cd plugin; mvn versions:set -DnewVersion=${POST_RELEASE_SNAPSHOT_VERSION})"
    sh "git add -A; git commit -m 'Post-release version bump'"
}

def push() {
    sshagent(["${GIT_CREDENTIALS_ID}"]) {
        sh "git push --set-upstream origin master; git push --tags"
    }
}

def cleanupWorkspace() {
    step([$class: 'WsCleanup'])
}

def cloneGitRepo() {
    sshagent(["${GIT_CREDENTIALS_ID}"]) {
        sh "git clone ${REPO_URL} ."
    }
}

def isNotTestOnly() {
    "${TEST_ONLY}" == "false"
}

def isNotDryRunOnly() {
    "${TEST_ONLY}" == "false" && "${DRY_RUN}" == "false"
}

pipeline {
    agent any
    tools {
        maven 'M3'
        jdk 'jdk8'
    }
    options {
        timestamps()
        skipDefaultCheckout()
    }
    stages {
        stage('Cleanup') {
            steps {
                cleanupWorkspace()
            }
        }
        stage('Clone') {
            steps {
                cloneGitRepo()
            }
        }
        stage('Purge') {
            steps {
                purge()
            }
        }
        stage('Set release version number') {
            when {
                expression {
                    isNotTestOnly()
                }
            }
            steps {
                setReleaseVersion()
            }
        }
        stage('Build') {
            steps {
                install()
            }
        }
        stage('Integration test') {
            steps {
                runCoreITs()
                runExtITs()
            }
        }
        stage('Tag release') {
            when {
                expression {
                    isNotTestOnly()
                }
            }
            steps {
                tagRelease()
            }
        }
        stage('Release artefacts') {
            when {
                expression {
                    isNotDryRunOnly()
                }
            }
            steps {
                release()
            }
        }
        stage('Purge - snapshot') {
            when {
                expression {
                    isNotTestOnly()
                }
            }
            steps {
                purge()
            }
        }
        stage('Set snapshot version number') {
            when {
                expression {
                    isNotTestOnly()
                }
            }
            steps {
                setSnapshotVersion()
            }
        }
        stage('Build - snapshot') {
            when {
                expression {
                    isNotTestOnly()
                }
            }
            steps {
                install()
            }
        }
        stage('Integration test - snapshot') {
            when {
                expression {
                    isNotTestOnly()
                }
            }
            steps {
                runCoreITs()
                runExtITs()
            }
        }
        stage('Push release to origin') {
            when {
                expression {
                    isNotDryRunOnly()
                }
            }
            steps {
                push()
            }
        }
    }
}