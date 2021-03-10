package protocol3.backend;

public class ServerMeta
{
	// -- SERVER STATISTICS -- //
	private static double _uptimeInSeconds = 0;

	public static void tickUptime(double msToAdd)
	{
		_uptimeInSeconds += msToAdd / 1000;
	}

	public static double getUptime()
	{
		return _uptimeInSeconds;
	}

}