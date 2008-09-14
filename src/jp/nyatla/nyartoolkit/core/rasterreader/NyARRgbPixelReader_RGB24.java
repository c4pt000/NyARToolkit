/* 
 * PROJECT: NyARToolkit
 * --------------------------------------------------------------------------------
 * This work is based on the original ARToolKit developed by
 *   Hirokazu Kato
 *   Mark Billinghurst
 *   HITLab, University of Washington, Seattle
 * http://www.hitl.washington.edu/artoolkit/
 *
 * The NyARToolkit is Java version ARToolkit class library.
 * Copyright (C)2008 R.Iizuka
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this framework; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 * 
 * For further information please contact.
 *	http://nyatla.jp/nyatoolkit/
 *	<airmail(at)ebony.plala.or.jp>
 * 
 */
package jp.nyatla.nyartoolkit.core.rasterreader;

import jp.nyatla.nyartoolkit.core.types.*;
/**
 * byte[]配列に、パディング無しの8bit画素値が、RGBRGBの順で並んでいる
 * バッファに使用できるピクセルリーダー
 *
 */
public class NyARRgbPixelReader_RGB24 implements INyARRgbPixelReader
{
	protected byte[] _ref_buf;

	private NyARIntSize _size;

	public NyARRgbPixelReader_RGB24(byte[] i_buf, NyARIntSize i_size)
	{
		this._ref_buf = i_buf;
		this._size = i_size;
	}

	public void getPixel(int i_x, int i_y, int[] o_rgb)
	{
		byte[] ref_buf = this._ref_buf;
		int bp = (i_x + i_y * this._size.w) * 3;
		o_rgb[0] = (ref_buf[bp + 0] & 0xff);// R
		o_rgb[1] = (ref_buf[bp + 1] & 0xff);// G
		o_rgb[2] = (ref_buf[bp + 2] & 0xff);// B
		return;
	}

	public void getPixelSet(int[] i_x, int[] i_y, int i_num, int[] o_rgb)
	{
		int width = this._size.w;
		byte[] ref_buf = this._ref_buf;
		int bp;
		for (int i = i_num - 1; i >= 0; i--) {
			bp = (i_x[i] + i_y[i] * width) * 3;
			o_rgb[i * 3 + 0] = (ref_buf[bp + 0] & 0xff);// R
			o_rgb[i * 3 + 1] = (ref_buf[bp + 1] & 0xff);// G
			o_rgb[i * 3 + 2] = (ref_buf[bp + 2] & 0xff);// B
		}
	}
}