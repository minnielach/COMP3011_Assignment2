package comp3011;

import java.util.concurrent.*;
import java.util.function.*;

// process the video effects using one effect
public class EffectProcessor implements Runnable {
	
	private final FrameProcessor processor; // the effect to apply to each frame 
	private final BlockingQueue<ProcessingFrame> inputFrames; // frames waiting to be processed 
	private final BlockingQueue<ProcessingFrame> outputFrames; // frames ready for the next effect
	private final LongSupplier currentGeneration; // get current playback generation
	
	public EffectProcessor (FrameProcessor processor, BlockingQueue<ProcessingFrame> inputFrames, BlockingQueue<ProcessingFrame> outputFrames, LongSupplier currentGeneration) {
		
		this.processor = processor; // store the effect 
		this.inputFrames = inputFrames; // store input queue
		this.outputFrames = outputFrames; // store output queue
		this.currentGeneration = currentGeneration; // store the playback generation
	}
	
	@Override
	// keep processing frames until the thread stops
	public void run() {
		try {
			while(!Thread.currentThread().isInterrupted()) {
				ProcessingFrame currentFrame = inputFrames.take(); // get the next frame
				
				// skip frames which belong to earlier
				if (currentFrame.generation() != currentGeneration.getAsLong()) {
					continue;
				}
				
				processor.process(currentFrame.frame(), currentFrame.info()); // apply effect
				
				// the user might have jumped while the effect was running 
				if (currentFrame.generation() != currentGeneration.getAsLong()) {
					continue;
				}
				
				outputFrames.put(currentFrame); // send frame to next processing stage
			}
		} catch (InterruptedException e) {
			Thread.currentThread().interrupt(); // stop thread
		} catch (Exception e) {
			throw new RuntimeException("Error proccessing frame", e); // report an error
		}
	}
}
