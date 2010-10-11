package jp.nyatla.nyartoolkit.dev.rpf.sampler.lowresolution;

import jp.nyatla.nyartoolkit.NyARException;
import jp.nyatla.nyartoolkit.core.raster.NyARGrayscaleRaster;
import jp.nyatla.nyartoolkit.core.types.NyARIntPoint2d;
import jp.nyatla.nyartoolkit.core.types.NyARIntRect;
import jp.nyatla.nyartoolkit.core.types.stack.*;
import jp.nyatla.nyartoolkit.dev.hierarchicallabeling.utils.NyARManagedObject;
import jp.nyatla.nyartoolkit.dev.hierarchicallabeling.utils.NyARManagedObjectPool;
import jp.nyatla.nyartoolkit.dev.hierarchicallabeling.utils.NyARManagedObject.INyARManagedObjectPoolOperater;

/**
 * LowResolutionLabelingSampler用の出力コンテナです。サンプリング結果を受け取ります。
 * 内容には、AreaDataItemの集合を持ちます。
 * AreaDataItemは元画像に対する、Labeling結果と元画像の情報のセットです。
 */
public class LowResolutionLabelingSamplerOut
{
	/**
	 * クラス内定義ができない処理系では、LowResolutionLabelingSamplerOutItemで定義してください。
	 *
	 */
	public class Item extends NyARManagedObject
	{
		/**
		 * ラべリング元の画像へのポインタです。
		 */
		public NyARGrayscaleRaster ref_raster;
		/**
		 * ラべリングした画像の解像度値です。
		 */
		public int resolution;
		/**
		 * ラべリング対象のエントリポイントです。
		 */
		public NyARIntPoint2d entry_pos=new NyARIntPoint2d();
		/**
		 * ラべリング対象の範囲を、トップレベル換算した値です。クリップ情報から計算されます。
		 */
		public NyARIntRect    base_area  =new NyARIntRect();
		/**
		 * ラべリング対象の範囲中心を、トップレベルに換算した値です。クリップ情報から計算されます。
		 */
//		public NyARIntPoint2d base_area_center=new NyARIntPoint2d();
		/**
		 * エリア矩形の対角距離の2乗値
		 */
		public int base_area_sq_diagonal;
		
		public int lebeling_th;
		
		public Item(INyARManagedObjectPoolOperater i_pool)
		{
			super(i_pool);
		}
	}	
	/**
	 * AreaのPoolクラス
	 *
	 */
	private class AreaPool extends NyARManagedObjectPool<Item>
	{
		public AreaPool(int i_length) throws NyARException
		{
			super.initInstance(i_length,Item.class);
			return;
		}
		protected Item createElement()
		{
			return new Item(this._inner_pool);
		}
	}
	/**
	 * AreaのStackクラス
	 *
	 */
	private class AreaStack extends NyARPointerStack<Item>
	{
		public AreaStack(int i_length) throws NyARException
		{
			super.initInstance(i_length, Item.class);
		}
	}
	/**
	 * 元
	 */
	public LrlsGsRaster ref_base_raster;
	private AreaPool _pool;
	private AreaStack _stack;

	public LowResolutionLabelingSamplerOut(int i_length) throws NyARException
	{
		this._pool=new AreaPool(i_length);
		this._stack=new AreaStack(i_length);
		return;
	}
	/**
	 * Samplerが使う関数です。ユーザは通常使用しません。
	 * SamplerOutの内容を初期状態にします。
	 * @param i_source
	 */
	public void initializeParams(LowResolutionLabelingSamplerIn i_source)
	{
		//基準ラスタの設定
		this.ref_base_raster=i_source.getRasterByDepth(0);
		
		Item[] items=this._stack.getArray();
		//スタック内容の初期化
		for(int i=this._stack.getLength()-1;i>=0;i--){
			items[i].releaseObject();
			items[i]=null;
		}
		//スタックをクリア
		this._stack.clear();
	}
	public Item prePush() throws NyARException
	{
		Item result=this._pool.newObject();
		if(result==null){
			return null;
		}
		if(this._stack.push(result)==null){
			result.releaseObject();
			return null;
		}
		return result;
		
	}
	/**
	 * 検出したエリアデータの配列を返します。
	 * @return
	 */
	public Item[] getArray()
	{
		return this._stack.getArray();
	}
	/**
	 * 検出したエリアデータの総数を返します。
	 * @return
	 */
	public int getLength()
	{
		return this._stack.getLength();
	}
}