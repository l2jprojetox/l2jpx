package com.px.gameserver.model.boat;

import com.px.commons.geometry.basic.Line2D;

import com.px.gameserver.model.location.BoatLocation;
import com.px.gameserver.model.location.Location;
import com.px.gameserver.model.location.Point2D;

public enum BoatDock
{
	TALKING_ISLAND(new BoatLocation(-96622, 261660, -3610, 150, 1800), new Location(-96777, 258970, -3623), new Line2D(-96614, 261418, -96883, 261416), new Line2D(230, 0, 230, -260), true),
	GLUDIN(new BoatLocation(-95686, 150514, -3610, 150, 800), new Location(-90015, 150422, -3610), new Line2D(-95441, 150530, -95442, 150761), new Line2D(-230, 0, -230, -260), true),
	RUNE(new BoatLocation(34381, -37680, -3610, 220, 800), new Location(34513, -38009, -3640), new Line2D(34557, -37875, 34406, -38028), new Line2D(230, 0, 230, -260), true),
	GIRAN(new BoatLocation(48950, 190613, -3610, 150, 800), new Location(46763, 187041, -3451), new Line2D(48850, 190383, 49055, 190280), new Line2D(-230, 0, -230, -260), false),
	PRIMEVAL(new BoatLocation(10342, -27279, -3610, 150, 1800), new Location(10447, -24982, -3664), new Line2D(10395, -27034, 10538, -27035), new Line2D(230, 0, 230, -260), false),
	INNADRIL(new BoatLocation(111384, 226232, -3610, 150, 800), new Location(107092, 219098, -3952), new Line2D(111137, 225989, 111387, 225991), new Line2D(230, -260, 230, 0), false);
	
	public static final BoatDock[] VALUES = values();
	
	private static final double IN_RATIO = 1.2;
	private static final double OUT_RATIO = 0.9;
	
	private final BoatLocation _dockLoc;
	private final Location _oustLoc;
	
	private final Line2D _dockEntrance;
	private final Line2D _boatEntrance;
	
	private final boolean _isBusyOnStart;
	
	private final double _angle;
	private final double _factor;
	
	private boolean _isBusy;
	
	BoatDock(BoatLocation dockLoc, Location oustLoc, Line2D dockEntrance, Line2D boatEntrance, boolean busyOnStart)
	{
		_dockLoc = dockLoc;
		_oustLoc = oustLoc;
		_dockEntrance = dockEntrance;
		_boatEntrance = boatEntrance;
		_isBusyOnStart = busyOnStart;
		
		final Point2D dockPoint = _dockEntrance.getPoint();
		final Point2D boatPoint = _boatEntrance.getPoint();
		
		_angle = dockPoint.calculateRelativeAngle(boatPoint);
		_factor = dockPoint.length() / boatPoint.length();
	}
	
	public BoatLocation getDockLoc()
	{
		return _dockLoc;
	}
	
	public Location getOustLoc()
	{
		return _oustLoc;
	}
	
	public Line2D getDockEntrance()
	{
		return _dockEntrance;
	}
	
	public Line2D getBoatEntrance()
	{
		return _boatEntrance;
	}
	
	public boolean isBusyOnStart()
	{
		return _isBusyOnStart;
	}
	
	public boolean isBusy()
	{
		return _isBusyOnStart && _isBusy;
	}
	
	public void setBusy(boolean status)
	{
		if (_isBusyOnStart)
			_isBusy = status;
	}
	
	public Point2D getBoardingPoint(Point2D origin, Point2D dest, boolean isInBoat)
	{
		return _dockEntrance.getAdjustedIntersectionPoint(origin, dest, isInBoat ? IN_RATIO : OUT_RATIO);
	}
	
	public Point2D getAdjustedBoardingPoint(Point2D origin, Point2D dest, boolean isInBoat)
	{
		final Point2D boardingPoint = getBoardingPoint(origin, dest, isInBoat);
		if (boardingPoint == null)
		{
			final double ratio = isInBoat ? IN_RATIO : OUT_RATIO;
			final int x = (int) Math.round(origin.getX() + ratio * (dest.getX() - origin.getX()));
			final int y = (int) Math.round(origin.getY() + ratio * (dest.getY() - origin.getY()));
			return new Point2D(x, y);
		}
		return boardingPoint;
	}
	
	public Point2D convertBoatToWorldCoordinates(int x, int y)
	{
		final Point2D point = new Point2D(x - _boatEntrance.getP1x(), y - _boatEntrance.getP1y());
		point.rotate(_angle);
		point.scale(_factor);
		
		point.setX(_dockEntrance.getP1x() + point.getX());
		point.setY(_dockEntrance.getP1y() + point.getY());
		return point;
	}
	
	public Point2D convertWorldToBoatCoordinates(int x, int y)
	{
		final Point2D point = new Point2D(x - _dockEntrance.getP1x(), y - _dockEntrance.getP1y());
		point.rotate(-_angle);
		point.scale(1 / _factor);
		
		point.setX(_boatEntrance.getP1x() + point.getX());
		point.setY(_boatEntrance.getP1y() + point.getY());
		return point;
	}
}