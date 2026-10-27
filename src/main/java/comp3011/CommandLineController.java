/*
 * Starter code supplied for Adelaide University COMP3011 Assignment 2.
 * Students are free to modify this file for assessment purposes.
 * 
 * Authors:
 *   1. Simon Ratcliffe, in collaboration with GPT-5.6 Terra
 *   2. Minnie Zhang-Lach, a2972746
 *
 * Copyright 2026 Simon Ratcliffe
 */
package comp3011;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Created in the VideoPlayerApp's main function to receive and parse the video player's command-line arguments.
 *
 * <p>
 * Extracts the video file and supported launch options, reports invalid input
 * or help text, and exposes the resulting launch configuration via methods to the
 * {@link VideoPlayerApp}.
 * </p>
 */
public class CommandLineController {
    private final String[] args;

    private boolean helpRequested;
    private boolean audioRequested;
    private boolean maximiseRequested;
    private Integer displayId;
    private File videoFile;
    private String errorMessage;
    
    // create an empty list which will store the video effects selected
    private final List<FrameProcessor> frameProcessors = new ArrayList<>();

    public CommandLineController(String[] args) {
        this.args = args.clone();
        parse();
        if (errorMessage != null) {
            System.out.println(errorMessage);
        }
        if (helpRequested) {
            printHelp();
        }
    }

    public File getVideoFile() {
        return videoFile;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public int getExitCode() {
        return errorMessage == null ? 0 : 1;
    }

    public Integer getDisplayId() {
        return displayId;
    }

    public boolean isAudioRequested() {
        return audioRequested;
    }

    public boolean isMaximiseRequested() {
        return maximiseRequested;
    }

    public boolean shouldLaunchApplication() {
        return errorMessage == null && videoFile != null;
    }
    
    public List<FrameProcessor> getFrameProcessors() {
    	return List.copyOf(frameProcessors);
    }

    private void parse() {
        List<String> videoFiles = new ArrayList<>();
        for (String arg : args) {
            if ("-h".equals(arg) || "--help".equals(arg)) {
                helpRequested = true;
            } else if ("-a".equals(arg) || "--audio".equals(arg)) {
                audioRequested = true;
            } else if ("-x".equals(arg) || "--maximise".equals(arg)) {
                maximiseRequested = true;
            } else if ("-1".equals(arg) || "--monitor-1".equals(arg)) {
                setDisplayId(1);
            } else if ("-2".equals(arg) || "--monitor-2".equals(arg)) {
                setDisplayId(2);
            }else if (arg.startsWith("-") && !arg.startsWith("--") && arg.length() > 1) {
            	
            	// check each letter after the dashes are removed
            	for (char effect : arg.substring(1).toCharArray()) {
                	// check is there is a match
                	switch (effect) {
                		case 'h' -> helpRequested = true;
                		case 'a' -> audioRequested = true;
                		case 'x' -> maximiseRequested = true;
                		case '1' -> setDisplayId(1);
                		case '2' -> setDisplayId(2);
                		
                		// show error if there is no exisiting option
                		default -> {
                			if (!addFrameProcessor(effect)) {
                				errorMessage = "Unknown Option: " + effect;
                			}
                		}
                	}
            	}
            	
            } else if (arg.startsWith("--")) {
            	
            	// check if the long effect matches a video effect
            	if (!addLongFrameProcessor(arg)) {
            		errorMessage = "Unknown option: " + arg;
            	}
            	
            } else if (arg.startsWith("-")) {
                errorMessage = "Unknown option: " + arg;
            } else {
                videoFiles.add(arg);
            }
        }

        if (videoFiles.size() > 1) {
            errorMessage = "Usage: VideoPlayer [options] [video-file]";
        } else if (videoFiles.size() == 1) {
            videoFile = new File(videoFiles.get(0));
            if (!videoFile.isFile()) {
                errorMessage = "File not found: " + videoFile.getPath();
                videoFile = null;
            }
        } else {
            if (!helpRequested) {
                errorMessage = "No video file specified.";
            }
        }
    }
    
    // check which long option matches the effect
    private boolean addLongFrameProcessor(String option) {
    	
    	// match the long option to the letter
    	Character effectOption = switch (option) {
	    	case "--number-frames" -> 'n';
	    	case "--scratch-frames" -> 's';
	    	case "--flicker-frames" -> 'f';
	    	case "--black-and-white" -> 'w';
	    	case "--yellow-frames" -> 'y';
	    	case "--vignette-frames" -> 'v';
	    	case "--dust-frames" -> 'd';
	    	case "--jitter-frames" -> 'j';
	    	case "--mottle-frames" -> 'm';
	    	case "--bleed-frames" -> 'b';
	    	case "--pepper-frames" -> 'p';
	    	default -> null;
    	};
    	
    	// check if the long option is valid
    	if (effectOption == null) {
    		return false;
    	}
    	
    	// add the effect to the list
    	return addFrameProcessor(effectOption);
    }
    
    // create and store the frame processor based on the selected option
    private boolean addFrameProcessor(char effectOption) {
    	    	
    	// match each option to its video effect
    	FrameProcessor effect = switch (effectOption) {
	    	case 'n' -> new FrameNumberer();
	    	case 's' -> new FrameScratcher();
	    	case 'f' -> new FrameFlickerer();
	    	case 'w' -> new FrameBlackAndWhiter();
	    	case 'y' -> new FrameYellower();
	    	case 'v' -> new FrameVignetter();
	    	case 'd' -> new FrameDuster();
	    	case 'j' -> new FrameJitterer();
	    	case 'm' -> new FrameMottler();
	    	case 'b' -> new FrameBleeder();
	    	case 'p' -> new FramePepperer();
	    	default -> null;
    	};
    	
    	// check if the option is valid
    	if (effect == null) {
    		return false;
    	}
    	
    	// store the selected option effect in the list
    	frameProcessors.add(effect);
    	return true;
    }

    private void setDisplayId(int displayId) {
        if (this.displayId != null && this.displayId != displayId) {
            errorMessage = "Only one display option can be used";
            return;
        }
        this.displayId = displayId;
    }

    // extended the help text 
    private void printHelp() {
        System.out.println("Usage: VideoPlayer [options] [video-file]");
        System.out.println();
        System.out.println("Options:");
        System.out.println("  -h, --help         Show this help message");
        System.out.println("  -a, --audio        Play audio");
        System.out.println("  -x, --maximise     Open the player maximised");
        System.out.println("  -1, --monitor-1    Open the player on display 1");
        System.out.println("  -2, --monitor-2    Open the player on display 2");
        System.out.println();
        System.out.println("Frame processors:");
        System.out.println("  -n, --number-frames    Render the frame number onto each frame");
        System.out.println("  -s, --scratch-frames   Render vertical film scratches");
        System.out.println("  -f, --flicker-frames   Randomly dim frames");
        System.out.println("  -w, --black-and-white  Convert frames to black and white");
        System.out.println("  -y, --yellow-frames    Apply a warmer colour temperature");
        System.out.println("  -v, --vignette-frames  Darken the frame edges");
        System.out.println("  -d, --dust-frames      Render dust and hair marks");
        System.out.println("  -j, --jitter-frames    Randomly displace frames by a few pixels");
        System.out.println("  -m, --mottle-frames    Add cloudy emulsion mottling");
        System.out.println("  -b, --bleed-frames     Bleed light into frames");
        System.out.println("  -p, --pepper-frames    Pepper frames with dark spots/blotches");
        System.out.println();
        System.out.println("Frame processors are applied in command-line order and may be repeated.");
        System.out.println("Example: -nssnfwyvdjmbp numbers, scratches twice, numbers again, flickers,");
        System.out.println("converts, warms, vignettes, dusts, jitters, mottles, bleeds, then peppers.");
        

    }
}
