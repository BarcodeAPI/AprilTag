/* Copyright (C) 2013-2016, The Regents of The University of Michigan.
All rights reserved.
This software was developed in the APRIL Robotics Lab under the
direction of Edwin Olson, ebolson@umich.edu. This software may be
available under alternative licensing terms; contact the address above.
Redistribution and use in source and binary forms, with or without
modification, are permitted provided that the following conditions are met:
1. Redistributions of source code must retain the above copyright notice, this
   list of conditions and the following disclaimer.
2. Redistributions in binary form must reproduce the above copyright notice,
   this list of conditions and the following disclaimer in the documentation
   and/or other materials provided with the distribution.
THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR
ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
(INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
(INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
The views and conclusions contained in the software and documentation are those
of the authors and should not be interpreted as representing official policies,
either expressed or implied, of the Regents of The University of Michigan.
*/

package org.barcodeapi.apriltag;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

public class GenerateTags {

	private static final String PACKAGE = "org.barcodeapi.apriltag.families";

	public static void main(String args[]) throws Exception {

		String tagType = args[0];

		// Lookup the tagFamily class by name
		Class<? extends TagFamily> clazz = Class.forName(//
				String.format("%s.%s", PACKAGE, tagType))//
				.asSubclass(TagFamily.class);

		// Create the tagFamily instance
		TagFamily tagFamily = clazz//
				.getDeclaredConstructor().newInstance();

		// Render the tags
		renderTagFamily(tagFamily);
	}

	public static void renderTagFamily(TagFamily tagFamily) {
		long timeStart = System.currentTimeMillis();

		// Create directory for tag family
		File tagDir = new File("tags", tagFamily.getFilePrefix());
		if (!tagDir.exists()) {
			tagDir.mkdirs();
		}

		// Print number of tags to be rendered
		int countTags = tagFamily.getCodes().length;
		System.out.printf("Generating %d tags for %s\n", //
				countTags, tagFamily.getFilePrefix());

		// Loop each of the codes in the family
		for (int tagId = 0; tagId < countTags; tagId++) {

			// Get code and render the image
			BufferedImage img = tagFamily.getLayout()//
					.renderToImage(tagFamily.getCodes()[tagId], 8);

			try {

				// Save image to file on disk
				ImageIO.write(img, "png", //
						new File(tagDir, String.format("%05d.png", tagId)));
			} catch (IOException ex) {
				System.out.println("ex: " + ex);
			}
		}

		// Calculate and print render time for family
		long timeRender = (System.currentTimeMillis() - timeStart);
		System.out.printf("Done: %dms\n\n", timeRender);
	}
}
