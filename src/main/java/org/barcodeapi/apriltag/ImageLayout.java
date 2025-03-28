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
ON ANY THEORY OF LIABILITY, WHETHER IN /CONTRACT, STRICT LIABILITY, OR TORT
(INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
The views and conclusions contained in the software and documentation are those
of the authors and should not be interpreted as representing official policies,
either expressed or implied, of the Regents of The University of Michigan.
*/

package org.barcodeapi.apriltag;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.image.BufferedImage;

public class ImageLayout {
	static final Color WHITE = new Color(255, 255, 255, 255);
	static final Color BLACK = new Color(0, 0, 0, 255);
	static final Color TRANSPARENT = new Color(0, 0, 0, 0);

	private String name;
	private int numBits;
	private int size;
	private String dataString;
	private int borderWidth;
	private int borderStart;
	private boolean reversedBorder;

	public enum PixelType {
		NOTHING, WHITE, BLACK, DATA
	}

	private PixelType[][] pixels;

	private ImageLayout() {
	}

	public static class Factory {
		public static ImageLayout createFromString(String name, String data) {
			ImageLayout layout = new ImageLayout();
			layout.name = name;
			layout.dataString = data;

			layout.size = (int) Math.sqrt(data.length());
			if (layout.size * layout.size != data.length()) {
				throw new RuntimeException("Layout string is not square.");
			}

			layout.pixels = new PixelType[layout.size][layout.size];
			layout.numBits = 0;
			int loc = 0;
			for (char c : data.toCharArray()) {
				PixelType pixelType = getPixelTypeForChar(c);
				if (pixelType == PixelType.DATA) {
					layout.numBits++;
				}
				layout.pixels[loc / layout.size][loc % layout.size] = pixelType;
				loc++;
			}

			// Validate symmetric under rot90
			for (int y = 0; y <= layout.size / 2; y++) {
				for (int x = y; x < layout.size - 1 - y; x++) {
					if (!(layout.pixels[y][x] == layout.pixels[x][layout.size - 1 - y]
							&& layout.pixels[y][x] == layout.pixels[layout.size - 1 - x][y]
							&& layout.pixels[y][x] == layout.pixels[layout.size - 1 - y][layout.size - 1 - x])) {
						throw new RuntimeException("Layout not symmetric.");
					}
				}
			}

			// Validate border
			boolean foundBorder = false;
			for (int i = 0; i < (layout.size - 1) / 2; i++) {
				if (layout.pixels[i][i] == PixelType.WHITE && layout.pixels[i + 1][i + 1] == PixelType.BLACK
						&& validateBorder(layout, i, false)) {
					foundBorder = true;
					layout.reversedBorder = false;
					layout.borderStart = i + 1;
					layout.borderWidth = layout.size - 2 * layout.borderStart;
					break;
				}
				if (layout.pixels[i][i] == PixelType.BLACK && layout.pixels[i + 1][i + 1] == PixelType.WHITE
						&& validateBorder(layout, i, true)) {
					foundBorder = true;
					layout.reversedBorder = true;
					layout.borderStart = i + 1;
					layout.borderWidth = layout.size - 2 * layout.borderStart;
					break;
				}
			}
			if (!foundBorder) {
				throw new RuntimeException("Layout has no border.");
			}

			return layout;
		}

		private static boolean validateBorder(ImageLayout layout, int loc, boolean reversed) {
			PixelType outside = reversed ? PixelType.BLACK : PixelType.WHITE;
			PixelType inside = reversed ? PixelType.WHITE : PixelType.BLACK;

			for (int x = loc; x < layout.size - loc - 1; x++) {
				if (layout.pixels[loc][x] != outside) {
					return false;
				}
			}

			for (int x = loc + 1; x < layout.size - loc - 2; x++) {
				if (layout.pixels[loc + 1][x] != inside) {
					return false;
				}
			}
			return true;
		}

		private static PixelType getPixelTypeForChar(char c) {
			switch (c) {
			case 'x':
				return PixelType.NOTHING;
			case 'd':
				return PixelType.DATA;
			case 'w':
				return PixelType.WHITE;
			case 'b':
				return PixelType.BLACK;
			default:
				throw new RuntimeException("Invalid character.");
			}
		}
	}

	public int getNumBits() {
		return numBits;
	}

	public static char[][] rotate90(char[][] im1) {
		char[][] im2 = new char[im1.length][im1.length];

		// Loop all data for X/Y
		for (int y = 0; y < im1.length; y++) {
			for (int x = 0; x < im1.length; x++) {

				// Update data in new matrix
				im2[im1.length - 1 - x][y] = im1[y][x];
			}
		}
		return im2;
	}

	public BufferedImage renderToImage(long code, int scale) {
		char[][] imageData = renderToArray(code);

		// Calculate new size
		int scaledSize = (size * scale);

		// Create new image canvas
		BufferedImage img = new BufferedImage(//
				scaledSize, scaledSize, //
				BufferedImage.TYPE_4BYTE_ABGR);
		Graphics g2d = img.getGraphics();

		// Loop all data for X/Y
		for (int y = 0; y < size; y++) {
			for (int x = 0; x < size; x++) {

				// Set color based on data
				switch (imageData[y][x]) {
				case 0:
					g2d.setColor(BLACK);
					break;
				case 1:
					g2d.setColor(WHITE);
					break;
				case 2:
					g2d.setColor(TRANSPARENT);
					break;
				default:
					throw new RuntimeException("Unknown image pixel color.");
				}

				// Draw the block
				g2d.fillRect(//
						(x * scale), //
						(y * scale), //
						scale, scale);
			}
		}

		return img;
	}

	/**
	 * Render to an int array. Used for rendering image output and also for
	 * computing complexity.
	 */
	public char[][] renderToArray(long code) {
		char[][] img = new char[size][size];

		for (int i = 0; i < 4; i++) {
			img = rotate90(img);
			// Render one-quarter of the image
			for (int y = 0; y <= size / 2; y++) {
				for (int x = y; x < size - 1 - y; x++) {
					char color;
					switch (pixels[y][x]) {
					case DATA:
						if ((code & (1L << (numBits - 1))) != 0) {
							color = 1;
						} else {
							color = 0;
						}
						code = code << 1;
						break;
					case BLACK:
						color = 0;
						break;
					case WHITE:
						color = 1;
						break;
					case NOTHING:
						color = 2;
						break;
					default:
						throw new RuntimeException("Impossible state");
					}
					img[y][x] = color;
				}
			}
		}

		// If there is a middle pixel, set it.
		if (size % 2 == 1) {
			char color;
			switch (pixels[size / 2][size / 2]) {
			case DATA:
				if ((code & (1L << (numBits - 1))) != 0) {
					color = 1;
				} else {
					color = 0;
				}
				break;
			case BLACK:
				color = 0;
				break;
			case WHITE:
				color = 1;
				break;
			case NOTHING:
				color = 2;
				break;
			default:
				throw new RuntimeException("Impossible state");
			}
			img[size / 2][size / 2] = color;
		}
		return rotate90(img);
	}

	public int getSize() {
		return size;
	}

	public String getName() {
		return name;
	}

	public String getDataString() {
		return dataString;
	}

	public boolean isReversedBorder() {
		return reversedBorder;
	}

	public int[][] getBitLocations() {
		int idx = 0;
		int[][] locations = new int[numBits][2];
		for (int y = 0; y < size / 2; y++) {
			for (int x = y; x < size - 1 - y; x++) {
				if (pixels[y][x] == PixelType.DATA) {
					locations[idx][0] = x;
					locations[idx][1] = y;
					idx++;
				}
			}
		}

		int step = numBits / 4;
		while (idx < step * 4) {
			locations[idx][0] = size - 1 - locations[idx - step][1];
			locations[idx][1] = locations[idx - step][0];
			idx++;
		}

		// Middle pixel.
		if (idx < numBits) {
			locations[idx][0] = size / 2;
			locations[idx][1] = size / 2;
		}

		// Shift the origin.
		for (idx = 0; idx < numBits; idx++) {
			locations[idx][0] -= borderStart;
			locations[idx][1] -= borderStart;
		}
		return locations;
	}

	public int getBorderWidth() {
		return borderWidth;
	}
}
