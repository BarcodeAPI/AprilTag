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

public class LayoutUtil {

	private static int l1DistToEdge(int x, int y, int size) {
		return Math.min(Math.min(x, size - 1 - x), Math.min(y, size - 1 - y));
	}

	public static ImageLayout getClassicLayout(int size) {
		StringBuilder sb = new StringBuilder();
		for (int y = 0; y < size; y++) {
			for (int x = 0; x < size; x++) {
				if (LayoutUtil.l1DistToEdge(x, y, size) == 0) {
					sb.append('w');
				} else if (LayoutUtil.l1DistToEdge(x, y, size) == 1) {
					sb.append('b');
				} else {
					sb.append('d');
				}
			}
		}
		// Classic layout has no name for backwards compatibility.
		return ImageLayout.Factory.createFromString("", sb.toString());
	}

	public static ImageLayout getStandardLayout(int size) {
		StringBuilder sb = new StringBuilder();
		for (int y = 0; y < size; y++) {
			for (int x = 0; x < size; x++) {
				if (LayoutUtil.l1DistToEdge(x, y, size) == 1) {
					sb.append('b');
				} else if (LayoutUtil.l1DistToEdge(x, y, size) == 2) {
					sb.append('w');
				} else {
					sb.append('d');
				}
			}
		}
		return ImageLayout.Factory.createFromString("Standard", sb.toString());
	}
}
