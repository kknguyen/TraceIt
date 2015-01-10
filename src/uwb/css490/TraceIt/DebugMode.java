package uwb.css490.TraceIt;

// Simple interface for debugging purposes. 
// Basically switches between debug numbers and our ideal 
// game numbers. 

// As of Nov 15 2014, it is used in DrawArea to determine 
// the bonus time given for completing a shape
// and in GameStatsFragment to determine the starting time
public interface DebugMode
{
	boolean debugModeNumbers = false;
}
