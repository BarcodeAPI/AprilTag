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

/** Generic class for all tag encoding families **/
public class TagFamily {

	/** The array of the codes. The id for a code is its index. **/
	private final long codes[];
	private final ImageLayout layout;

	/**
	 * Minimum hamming distance between any two codes (accounting for rotational
	 * ambiguity? The code can recover (minHammingDistance-1)/2 bit errors.
	 **/
	public final int minimumHammingDistance;

	/**
	 * Constructor for tags generated with previous AprilTag versions.
	 */
	public TagFamily(int area, int minimumHammingDistance, long codes[]) {
		this(LayoutUtil.getClassicLayout((int) Math.sqrt(area) + 4), //
				minimumHammingDistance, upgradeCodes(codes, (int) Math.sqrt(area)));
	}

	/**
	 * The codes array is not copied internally and so must not be modified
	 * externally.
	 **/
	public TagFamily(ImageLayout layout, int minimumHammingDistance, long codes[]) {
		this.layout = layout;

		this.minimumHammingDistance = minimumHammingDistance;
		this.codes = codes;
	}

	static long upgradeCode(long oldCode, int[][] bitLocations, int size) {
		long code = 0;

		for (int i = 0; i < bitLocations.length; i++) {
			code = code << 1;
			boolean val = (oldCode & (1L << (size - bitLocations[i][0] + (size - bitLocations[i][1]) * size))) > 0;
			if (val) {
				code |= 1;
			}
		}

		return code;
	}

	static long[] upgradeCodes(long[] oldCodes, int size) {
		int bitLocations[][] = LayoutUtil.getClassicLayout(size + 4).getBitLocations();
		long fixed_codes[] = new long[oldCodes.length];
		for (int i = 0; i < oldCodes.length; i++) {
			fixed_codes[i] = upgradeCode(oldCodes[i], bitLocations, size);
		}
		return fixed_codes;
	}

	/**
	 * Assuming we are draw the image one quadrant at a time, what would the rotated
	 * image look like? Special care is taken to handle the case where there is a
	 * middle pixel of the image.
	 */
	public static long rotate90(long w, int numBits) {
		int p = numBits;
		int l = 0;
		if (numBits % 4 == 1) {
			p = numBits - 1;
			l = 1;
		}
		w = ((w >> l) << (p / 4 + l)) | (w >> (3 * p / 4 + l) << l) | (w & l);
		w &= ((1L << numBits) - 1);
		return w;
	}

	public long[] getCodes() {
		return codes;
	}

	public String getFilePrefix() {
		return String.format("tag%dh%d", //
				layout.getNumBits(), minimumHammingDistance);
	}

	public ImageLayout getLayout() {
		return layout;
	}
}
