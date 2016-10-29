package fr.amille.amiout.entity;

public class FileInformation {

	private String fileName;

	private int fileSize;

	private int areaW;

	private int areaH;

	public FileInformation(final String fileInformations) {

		final String[] fileInfos = fileInformations.split("\\n");

		if (fileInfos.length != 5 || fileInfos[0].isEmpty()) {

			throw new IllegalArgumentException(
					"File informations malformed, should have 5 parameters, found: " + fileInfos.length);
		}

		setFileName(fileInfos[0]);
		setFileSize(Integer.parseInt(fileInfos[1]));
		setAreaW(Integer.parseInt(fileInfos[2]));
		setAreaH(Integer.parseInt(fileInfos[3]));
	}

	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	public int getFileSize() {
		return fileSize;
	}

	public void setFileSize(int fileSize) {
		this.fileSize = fileSize;
	}

	public int getAreaW() {
		return areaW;
	}

	public void setAreaW(int areaW) {
		this.areaW = areaW;
	}

	public int getAreaH() {
		return areaH;
	}

	public void setAreaH(int areaH) {
		this.areaH = areaH;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "FileInformation [fileName=" + fileName + ", fileSize=" + fileSize + ", areaW=" + areaW + ", areaH="
				+ areaH + "]";
	}

}
