package jp.nyatla.nyartoolkit.core.kpm.sandbox.matrixlab;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MatrixCodeGen10 extends MatTable{
	public MatrixCodeGen10(int i_size) {
		super(i_size);
	}
	/**
	 * ベースクラス
	 */
	static abstract public class Factor{
		public int lv;
		public int idx;
		private static int serial=0;
		public boolean zero;
		public Factor(int i_lv,boolean i_zero ){
			this.lv=i_lv;
			this.idx=serial;
			this.zero=i_zero;
			serial++;
		}
		abstract public String toString();
		public boolean equals(Object e)
		{
			Factor te=((Factor)e);
			return te.toString().compareTo(this.toString())==0;
		}
		public int hashCode()
		{
			return this.toString().hashCode();
		}		
	}
	public class ValueFactor extends Factor{
		public String val;
		public ValueFactor(String i_val,int i_lv){
			super(i_lv,i_val.compareTo("0")==0);
			this.val=i_val;
		}
		@Override
		public String toString()
		{
			return val;
		}
	}	
	/** n項の和*/
	public class SumFactor extends Factor
	{
		class Item{
			final public int flag;
			final public Factor factor;
			Item(int i_flag,Factor i_factor){
				this.flag=i_flag;
				this.factor=i_factor;
			}
		}
		final List<Item> l=new ArrayList<Item>();
		public SumFactor()
		{
			super(0,true);
		}
		public void add(int i_flag,Factor f){
			if(f.lv+1>this.lv){
				this.lv=f.lv+1;
			}
			this.l.add(new Item(i_flag,f));
			this.zero=false;
		}
		@Override
		public String toString()
		{
			String s="";
			for(Item i:this.l){
				s+=(i.flag>0?"+":"-")+i.factor.toString();
			}
			return "("+s.substring(1)+")";
		}
		public boolean allZero(){
			for(Item f : this.l){
				if(!f.factor.zero){
					return false;
				}
			}
			return true;
		}
		public int size(){
			return this.l.size();
		}

	}
	/** 2項の積*/
	public class MulSection extends Factor{
		final public Factor first;
		final public Factor second;
		final int first_flag;
		final int second_flag;
		public MulSection(int i_ff,Factor f,int i_fs,Factor s){
			super(0,f.zero || s.zero);
			this.first=f;
			this.first_flag=i_ff;
			this.second=s;
			this.second_flag=i_fs;
			this.lv=(int)Math.max(this.first.lv,this.second.lv)+1;
		}
		@Override
		public String toString()
		{
			String f1=this.first_flag>0?"":"-";
			String f2=this.second_flag>0?"":"-";
			return f1+first.toString()+"*"+f2+second.toString();
		}
	}
	public class Tag{
		public int vid;
		public Factor src_section;
		public Factor dst_section;
		public Tag(int i_value_id,Factor s){
			this.vid=i_value_id;
			this.src_section=s;
			this.dst_section=new ValueFactor("v"+i_value_id,s.lv);
			return;
		}

	}
	public class ElementMap extends HashMap<Factor,Tag>
	{
		private int serial_number=0;
		private static final long serialVersionUID = -5465103849523547316L;
		public Factor register(Factor i_key)
		{
			Tag t=null;
			if(this.get(i_key)==null){
				t=new Tag(serial_number,i_key);
				this.put(i_key,t);
				serial_number++;
			}else{
				t=this.get(i_key);
			}
			if(i_key.zero){
				return t.src_section;
			}else{
				return t.dst_section;
			}
//			return t.src_section;
		}
	
	}
	protected MatrixCodeGen10(int i_size,MatItem[][] t) {
		super(i_size,t);
	}


	/**
	 * 絶対値の文字列を返す。
	 * @return
	 */
	public Factor absStr(ElementMap map){
		if(this._size==2){
			MatItem s11=this._table[0][0];
			MatItem s12=this._table[0][1];
			MatItem s21=this._table[1][0];
			MatItem s22=this._table[1][1];
			String v3;
			if((!s11.is_zero) && (!s22.is_zero) && (!s21.is_zero) && (!s12.is_zero)){
				v3=String.format("(%s*%s-%s*%s)",s11.getStr(),s22.getStr(),s12.getStr(),s21.getStr());
			}else if(!s11.is_zero && !s22.is_zero){
				v3=String.format("(%s*%s)",s11.getStr(),s22.getStr());
			}else if(!s21.is_zero && !s12.is_zero){
				v3=String.format("(-%s*%s)",s12.getStr(),s21.getStr());
			}else{
				v3="0";
			}
			Factor r=new ValueFactor(v3,0);
			return map.register(r);
		}else{
			SumFactor ss=new SumFactor();
			for(int i=0;i<this._size;i++){
				MatrixCodeGen10 c=new MatrixCodeGen10(this._size-1,this.getCofactor(0,i));
				ValueFactor v;
				if(this._table[0][i].is_zero){
					v=new ValueFactor("0",0);
				}else{
					v=new ValueFactor(this._table[0][i].getStr(),0);
				}
				
				Factor s=new MulSection(
					1,
					v,
					1,
					c.absStr(map));
				if(s.zero){
					ss.add(1,new ValueFactor("0",0));
				}else{
					ss.add(this.getCofactorFlag(0, i),s);
				}
			}
			if(ss.allZero()){
				return map.register(new ValueFactor("0",0));
			}
			if(ss.size()>0){
				return map.register(ss);
			}else{
				return map.register(new ValueFactor("0",0));
			}
		}
	}
	public String inversMatrix()
	{
		ElementMap map=new ElementMap();
		String matstr="double det="+this.absStr(map).toString()+";\n";
		for(int r=0;r<this._size;r++){
			for(int c=0;c<this._size;c++){
				int f=this.getCofactorFlag(r, c);
				Factor val=new MatrixCodeGen10(this._size-1,this.getCofactor(r, c)).absStr(map);
				//名前は転値
				matstr+=String.format("this.m%d%d=(%s%s)/det;\n",c,r,f>0?"":"-", val);
			}				
		}
		//MAPの結果をListに転送
		List<Map.Entry<Factor, Tag>> l=new ArrayList<Map.Entry<Factor, Tag>>();
		for(Map.Entry<Factor, Tag> e : map.entrySet()){
			l.add(e);
		}/*
		//順番にソート
		Collections.sort(l, new Comparator<Map.Entry<Factor, Tag>>(){			 
            @Override
            public int compare(
            	Map.Entry<Factor, Tag> entry1, Map.Entry<Factor, Tag> entry2) {
            	int t=entry1.getValue().dst_section.lv-entry2.getValue().dst_section.lv;
                return t==0?0:(t>1?1:-1);
            }
        });*/
		Collections.sort(l, new Comparator<Map.Entry<Factor, Tag>>(){			 
            @Override
            public int compare(
            	Map.Entry<Factor, Tag> entry1, Map.Entry<Factor, Tag> entry2) {
            	int t=entry1.getValue().dst_section.idx-entry2.getValue().dst_section.idx;
                return t==0?0:(t>1?1:-1);
            }
        });
		
		String elemstr="";
		for(Map.Entry<Factor, Tag> e : l){
			Tag t=map.get(e.getKey());
			elemstr+=("double "+t.dst_section+"="+t.src_section+";//"+t.dst_section.idx+"\n");
		}
		return elemstr+matstr;
	}
	public static void main(String[] args){
		for(int r=0;r<8;r++){
			System.out.print("double ");
			for(int c=0;c<8;c++){
				System.out.print(String.format("a%d%d=this.m%d%d,",r,c,r,c));
			}
			System.out.println();
		}
		
		
		
		MatrixCodeGen10 m33=new MatrixCodeGen10(8);
		m33.setZero(0,0);m33.setZero(0,1);m33.setZero(0,2);
		m33.setZero(1,3);m33.setZero(1,4);m33.setZero(1,5);

		m33.setZero(2,0);m33.setZero(2,1);m33.setZero(2,2);
		m33.setZero(3,3);m33.setZero(3,4);m33.setZero(3,5);

		m33.setZero(4,0);m33.setZero(4,1);m33.setZero(4,2);
		m33.setZero(5,3);m33.setZero(5,4);m33.setZero(5,5);
		
		m33.setZero(6,0);m33.setZero(6,1);m33.setZero(6,2);
		m33.setZero(7,3);m33.setZero(7,4);m33.setZero(7,5);
		String s=m33.inversMatrix();
		System.out.println(s);

		try {
			File file = new File("d:\\mat.txt");
			FileWriter filewriter = new FileWriter(file);
			filewriter.write(s);
			filewriter.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}		
	}

}
