AprilTag-Generation
===================

A stripped down version of [AprilTag 3](https://github.com/AprilRobotics/apriltag-generation) for use in the BarcodeAPI.org server.

---

Run as a JAR to render all tags in a family.

```
java -jar apriltag.jar <tagFamily>

java -jar apriltag.jar Tag16h5
java -jar apriltag.jar TagCircle21h7
java -jar apriltag.jar TagStandard52h13
```

---

Include and use as a library to generate individual tags at runtime.

```
int tagID = 38;
TagFamily tagFamily = new Tag36h11();
BufferedImage tagImage = tagFamily.getLayout()//
		.renderToImage(tag.getCodes()[tagID], 8);
```
