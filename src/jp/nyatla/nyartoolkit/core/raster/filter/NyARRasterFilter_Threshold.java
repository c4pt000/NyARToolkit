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
package jp.nyatla.nyartoolkit.core.raster.filter;

import jp.nyatla.nyartoolkit.NyARException;
import jp.nyatla.nyartoolkit.core.raster.NyARRaster;
import jp.nyatla.nyartoolkit.core.types.TNyARIntSize;

/**
 * 定数閾値による2値化をする。
 *
 */
public class NyARRasterFilter_Threshold implements INyARRasterFilter
{
    private int _threshold;
    public NyARRasterFilter_Threshold(int i_threshold)
    {
	this._threshold=i_threshold;
    }
    public void doFilter(NyARRaster i_input,NyARRaster i_output) throws NyARException
    {
	assert(i_input.getBufferType()==NyARRaster.BUFFERFORMAT_INT2D);
	assert(i_output.getBufferType()==NyARRaster.BUFFERFORMAT_INT2D);
	assert(i_input.getSize().isEqualSize(i_output.getSize())==true);
	
	int[][] out_buf=(int [][])i_output.getBufferObject();
	int[][] in_buf=(int[][])i_input.getBufferObject();
	
	int bp=0;
	TNyARIntSize size=i_output.getSize();
	for(int y=0;y<size.h-1;y++){
	    for(int x=0;x<size.w;x++){
		out_buf[y][x]=in_buf[y][x]>this._threshold?255:0;
                bp+=3;
	    }
	}
	return;
    }
}
