package screens;

import java.awt.*;
import java.io.Serializable;

public class ViewDimension implements Serializable {

	// X, Y, Width, Height 를 포함
	public int x;
	public int y;
	public int width;
	public int height;

	public void useDisplayDimension() {
		Dimension size = Toolkit.getDefaultToolkit().getScreenSize();
		width = size.width;
		height = size.height;
		x = 0;
		y = 0;
	}

	// JSON 형태로 반환
	public String toString() {
		String s = "{\"X\":" + x + ",\"Y\":" + y + ", \"WIDTH\":" + width + ", \"HEIGHT\":" + height + "}";
		return s;
	}

	public ViewDimension(){}

	public ViewDimension(int width, int height) {
		this.width = width;
		this.height = height;
	}

	public ViewDimension(int x, int y, int width, int height) {
		this.x = x;
		this.y = y;
		this.width = width;
		this.height = height;
	}

	private ViewDimension getNewWindowPosition(int xPositionOfCenter, int yPositionOfCenter) {
		ViewDimension vd = new ViewDimension();
		vd.x = xPositionOfCenter - (width/2);
		vd.y = yPositionOfCenter - (height/2);
		vd.width = width;
		vd.height = height;
		return vd;
	}

	// 화면의 센터로 위치
	public void alignCenter(ViewDimension parentDimension) {
		ViewDimension vd = getNewWindowPosition(parentDimension.width / 2, parentDimension.height / 2);
		x = vd.x;
		y = vd.y;
		width = vd.width;
		height = vd.height;
	}

	public void moveByPercent(ViewDimension screenDimension, int percentInX, int percentInY) {
		int onePercentForX = screenDimension.width / 100;
		int onePercentForY = screenDimension.height / 100;

		x += percentInX * onePercentForX;
		y -= percentInY * onePercentForY;
	}

	public void toLeft(ViewDimension screenDimension) {
		x = 0;
	}

	public void toRight(ViewDimension screenDimension) {
		x = screenDimension.width - width;
	}

	public void toBottom(ViewDimension screenDimension) {
		y = screenDimension.height - height;
	}

	public void toTop(ViewDimension screenDimension) {
		y = 0;
	}

	public void scale(float scale) {
		width *= scale;
		height *= scale;
	}
}
