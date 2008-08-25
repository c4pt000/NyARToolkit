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
import jp.nyatla.nyartoolkit.core.types.*;
/**
 * 平均移動法を使った２値化フィルタ
 *
 */
public class NyARRasterFilter_AreaAverage implements INyARRasterFilter
{
    private int _area=4;
    public void doFilter(NyARRaster i_input,NyARRaster i_output) throws NyARException
    {
	assert(i_input.getBufferType()==NyARRaster.BUFFERFORMAT_INT2D);
	assert(i_output.getBufferType()==NyARRaster.BUFFERFORMAT_INT2D);

	TNyARIntSize size=i_output.getSize();
	assert(i_input.getSize().isEqualSize(i_output.getSize())==true);
	assert(size.h%8==0 && size.w%8==0);
		
	int[][] out_buf=(int [][])i_output.getBufferObject();
	int[][] in_buf=(int[][])i_input.getBufferObject();
	
	
	int area=this._area;
	int y1=area;
	int x1=area;
	int y2=size.h-area;
	int x2=size.w-area;
	
	for(int y=y1;y<y2;y++){
	    int sum,nn;
	    sum=nn=0;
	    for(int yy=y-area;yy<y+area+1;yy++){
		for(int xx=x1-area;xx<x1+area;xx++){
                    sum+=in_buf[yy][xx];
                    nn++;
		}
	    }
	    boolean first=true;
	    for(int x=area;x<x2;x++){
		if(!first){
		    for(int yy=y-area;yy<y+area;yy++){
			sum+=in_buf[yy][x+area];
			sum-=in_buf[yy][x-area];
		    }
		}
		first=false;
		int th=(sum/nn);
		
		int g=in_buf[y][x];
		out_buf[y][x]=th<g?255:0;
	    }
	}
	return;
    }    

}
