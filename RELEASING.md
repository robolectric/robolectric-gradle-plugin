# Release Checklist (Mac OSX specific)

0. Make sure Travis is passing and the version in build.gradle is bumped

1. Install GPG; homebrew works: 'brew install gpg'

2. Create or retrieve GPG keypair; see the [sonatype docs](https://docs.sonatype.org/display/Repository/Sonatype+OSS+Maven+Repository+Usage+Guide) for more info on how to use GPG for signing.

3. Check your GPG install works by typing 'gpg --list-keys'. You should see something like this:

    ```
   	/Users/pivotal/.gnupg/pubring.gpg
    ---------------------------------
    pub   4096R/FAE67CFD 2013-08-30 [expires: 2018-08-29]
    uid                  Pivotal Robolectric (Mike and Corey) <android-developers@pivotallabs.com>
    sub   4096R/87EAC09D 2013-08-30 [expires: 2018-08-29]
    ```

4. Set up passwords and other stuff in ~/.gradle/gradle.properties:

    ```
    nexusUsername=<your nexus username>
    nexusPassword=<your nexus password>
    signing.keyId=<id matching above output of gpg --list-keys>
    signing.password=<gpg private key passphrase>
    signing.secretKeyRingFile=<home directory>/.gnupg/secring.gpg
    ```

5. Run ./gradlew uploadArchives

When successful, the artifact will be in a staging repository on oss.sonatype.org.

6. Promote the artifact on Sonatype

  1. Go to http://oss.sonatype.org and login.
  2. Find the staging repository by clicking (on the left) on 'Staging Repositories' under 'Build Promotion'. The newly pushed artifact should be the last item on the list.
  3. On the toolbar up top, press 'close'. This operation will take a while so you may need to refresh, but eventually the staging repository will be "closed" - look under the 'activity' tab when you've got it selected and you should see the various steps of the closing process
  4. Once closed, you can press 'Release' in the toolbar. Again this will take some time, but when it's done there will no longer be an entry in the 'staging repositories' section. You can find the newly published artifact by browsing the normal repository.
  5. Wait. It will take some time before it appears on Maven Central - usually a few hours but less than a day.
