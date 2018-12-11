package org.apache.storm.timewindow;

public class Vector2DM {
	private int x;
	private int y;
	
	public Vector2DM(int vecX, int vecY) {
		x = vecX;
		y = vecY;
	}
	
	public Vector2DM(int startX, int startY, int endX, int endY) {
		x = endX - startX;
		y = endY - startY;
	}
	
	int NormOfVector()
	{
	    return (int)Math.sqrt(Math.pow(x, 2) + Math.pow(y, 2));
	}

	int VectorDot(Vector2DM src)
	{
	    return x * src.GetX() + y * src.GetY();
	}

	void VectorPlus(Vector2DM src)
	{
	    x += src.GetX();
	    y += src.GetY();
	}

	Vector2DM NormalVector()
	{
	    return new Vector2DM(y, -x);
	}

	void NormalizeVector()
	{
	    int normV = NormOfVector();
	    SetX(GetX() / normV);
	    SetY(GetY() / normV);
	}
	
    public int GetX() { return x; }
    public void SetX(int val) { x = val; }
    public int GetY() { return y; }
    public void SetY(int val) { y = val; }
}
