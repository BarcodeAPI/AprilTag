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

import org.barcodeapi.apriltag.families.Tag16h5;
import org.barcodeapi.apriltag.families.Tag25h9;
import org.barcodeapi.apriltag.families.Tag36h10;
import org.barcodeapi.apriltag.families.Tag36h11;
import org.barcodeapi.apriltag.families.Tag36h9;
import org.barcodeapi.apriltag.families.TagCircle21h7;
import org.barcodeapi.apriltag.families.TagCircle49h12;
import org.barcodeapi.apriltag.families.TagCustom48h12;
import org.barcodeapi.apriltag.families.TagStandard41h12;
import org.barcodeapi.apriltag.families.TagStandard52h13;

public class GenerateTags {
	public static void main(String args[]) {

		render(new Tag16h5());
		render(new Tag25h9());
		render(new Tag36h9());
		render(new Tag36h10());
		render(new Tag36h11());
		render(new TagCircle21h7());
		render(new TagCircle49h12());
		render(new TagCustom48h12());
		render(new TagStandard41h12());
		render(new TagStandard52h13());
	}

	public static void render(TagFamily tagFamily) {

		File f = new File(tagFamily.getFilePrefix());
		if (!f.exists()) {
			f.mkdirs();
		}

		System.out.println("Writing: " + tagFamily.getFilePrefix());
		int count = tagFamily.getCodes().length;

		long start = System.currentTimeMillis();

		for (int id = 0; id < count; id++) {

			render(tagFamily, id);
		}

		long process = (System.currentTimeMillis() - start);

		System.out.println("Done: " + process + "ms");
	}

	public static void render(TagFamily tag, int id) {

		BufferedImage img = tag.getLayout().renderToImage(tag.getCodes()[0]);
		String fname = String.format("%05d.png", id);

		try {
			ImageIO.write(img, "png", new File(tag.getFilePrefix(), fname));
		} catch (IOException ex) {
			System.out.println("ex: " + ex);
		}
	}
}
