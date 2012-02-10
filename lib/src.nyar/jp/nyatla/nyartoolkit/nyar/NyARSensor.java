package jp.nyatla.nyartoolkit.nyar;


import jp.nyatla.nyartoolkit.NyARException;
import jp.nyatla.nyartoolkit.core.param.*;
import jp.nyatla.nyartoolkit.core.raster.*;
import jp.nyatla.nyartoolkit.core.raster.rgb.*;
import jp.nyatla.nyartoolkit.core.rasterdriver.INyARHistogramFromRaster;
import jp.nyatla.nyartoolkit.core.rasterfilter.rgb2gs.*;
import jp.nyatla.nyartoolkit.core.types.*;

//javaImage対応のRgbAveを作ること。


public class NyARSensor
{
	protected NyARHistogram _gs_hist;
	private INyARRgbRaster _ref_raster;
	protected INyARGrayscaleRaster _gs_raster;
	private long _src_ts;
	private long _gs_id_ts;
	private long _gs_hist_ts;
	private NyARParam _ref_param;
	public NyARSensor(NyARParam i_param) throws NyARException
	{
		this.initInstance(i_param);
		this._hist_drv=(INyARHistogramFromRaster) this._gs_raster.createInterface(INyARHistogramFromRaster.class);
	}
	/**
	 * 画像ドライバに依存するインスタンスの生成。
	 * 継承クラスで上書きする。
	 * @param s
	 * @throws NyARException
	 */
	protected void initResource(NyARIntSize s) throws NyARException
	{
		this._gs_raster=new NyARGrayscaleRaster(s.w,s.h,NyARBufferType.INT1D_GRAY_8,true);
	}
	/**
	 * 共通初期化関数。
	 * @param i_param
	 * @param i_drv_factory
	 * ラスタドライバのファクトリ。
	 * @param i_gs_type
	 * @param i_rgb_type
	 * @return
	 * @throws NyARException
	 */
	private void initInstance(NyARParam i_param) throws NyARException
	{
		//リソースの生成
		this.initResource(i_param.getScreenSize());
		this._ref_param=i_param;
		this._gs_hist=new NyARHistogram(256);
		this._src_ts=0;
		this._gs_id_ts=0;
		this._gs_hist_ts=0;
	}
	
	private INyARHistogramFromRaster _hist_drv=null;	
	private INyARRaster _last_input_rasster=null;
	private INyARRgb2GsFilter _rgb2gs=null;
	/**
	 * この関数は、入力画像を元に、インスタンスの状態を更新します。
	 * この関数は、タイムスタンプをインクリメントします。
	 * @param i_input
	 * @throws NyARException 
	 */
	public void update(INyARRgbRaster i_input) throws NyARException
	{
		//ラスタドライバの準備
		if(this._last_input_rasster!=i_input){
			this._rgb2gs=(INyARRgb2GsFilter) i_input.createInterface(INyARRgb2GsFilter.class);
			this._last_input_rasster=i_input;
		}
		//RGB画像の差し替え
		this._ref_raster=i_input;
		//ソースidのインクリメント
		this._src_ts++;
	}
	/**
	 * この関数は、タイムスタンプを強制的にインクリメントします。
	 */
	public void updateTimeStamp()
	{
		this._src_ts++;
	}
	/**
	 * 現在のタイムスタンプを返します。
	 * @return
	 */
	public long getTimeStamp()
	{
		return this._src_ts;
	}
	/**
	 * ARToolkitのパラメータオブジェクトの参照値を返します。
	 * @return
	 */
	public NyARParam getARParam()
	{
		return this._ref_param;
	}
	/**
	 * この関数は、グレースケールに変換した現在の画像を返します。
	 * @return
	 * @throws NyARException 
	 */
	public INyARGrayscaleRaster getGsImage() throws NyARException
	{
		//必要に応じてグレースケール画像の生成
		if(this._src_ts!=this._gs_id_ts){
			this._rgb2gs.convert(this._gs_raster);
			this._gs_id_ts=this._src_ts;
		}
		return this._gs_raster;
		//
	}
	/**
	 * この関数は、現在のGS画像のﾋｽﾄｸﾞﾗﾑを返します。
	 * @throws NyARException 
	 */
	public NyARHistogram getGsHistogram() throws NyARException
	{
		//必要に応じてヒストグラムを生成
		if(this._gs_id_ts!=this._gs_hist_ts){
			this._hist_drv.createHistogram(4,this._gs_hist);
			this._gs_hist_ts=this._gs_id_ts;
		}
		return this._gs_hist;
	}
	/**
	 * 現在の入力画像の参照値を返します。
	 * @return
	 */
	public INyARRgbRaster getSourceImage()
	{
		return this._ref_raster;
	}
	/**
	 * 4頂点からRGBパターンを取得します。
	 */
	public void getRgbPatt(int i_x,int i_y,INyARRaster i_out)
	{
		
	}	
}