package com.zlcdgroup.photos;

/**
 * GridView的每个item的数据对象
 *
 * @author len
 *
 */
public class ImageBean{
	/**
	 * 文件夹的第一张图片路径
	 */
	private String topImagePath;
	/**
	 * 文件夹名
	 */
	private String folderName;
	/**
	 * 文件夹中的图片数
	 */
	private int imageCounts;

	//此文件夹的路径
	private String  fa_filepath;

	public String getTopImagePath() {
		return topImagePath;
	}
	public void setTopImagePath(String topImagePath) {
		this.topImagePath = topImagePath;
	}
	public String getFolderName() {
		return folderName;
	}
	public void setFolderName(String folderName) {
		this.folderName = folderName;
	}
	public int getImageCounts() {
		return imageCounts;
	}
	public void setImageCounts(int imageCounts) {
		this.imageCounts = imageCounts;
	}
	public String getFa_filepath() {
		return fa_filepath;
	}
	public void setFa_filepath(String fa_filepath) {
		this.fa_filepath = fa_filepath;
	}

}
