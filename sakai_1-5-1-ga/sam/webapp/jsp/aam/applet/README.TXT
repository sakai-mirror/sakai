IMPORTANT

The CapturePlayback.jar is created using the ant build script in Navigo/audio, 
which places the result in the temporary deployment directory.

	Navigo/audio/conf 

This sub project build script is now called from the main Navigo build
which transfers this jar file to the correct target.

NOTE:
This uses an audio.properties file which sets the user and password.

This properties file is built in the jar archive.

For production deployment a different user and password should be used in the 

	Navigo/audio/conf 

