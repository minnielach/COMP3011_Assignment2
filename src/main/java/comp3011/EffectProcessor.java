package comp3011;

import java.util.concurrent.*;

// process the video effects using one effect
public class EffectProcessor implements Runnable {
	
	private final FrameProcessor processor; // the effect to apply to each frame 
	private final BlockingQueue<ProcessingFrame> inputFrames; // frames waiting to be processed 
	private final BlockingQueue<ProcessingFrame> outputFrames; // frames ready for the next effect
	
	public EffectProcessor (FrameProcessor processor, BlockingQueue<ProcessingFrame> inputFrames, BlockingQueue<ProcessingFrame> outputFrames) {
		
		this.processor = processor; // store the effect 
		this.inputFrames = inputFrames; // store input queue
		this.outputFrames = outputFrames; // store output queue
	}
	
	@Override
	// keep processing frames until the thread stops
	public void run() {
		try {
			while(!Thread.currentThread().isInterrupted()) {
				ProcessingFrame currentFrame = inputFrames.take(); // get the next frame
				
				processor.process(currentFrame.frame(), currentFrame.info()); // apply effect
				
				outputFrames.put(currentFrame);
			}
		} catch (InterruptedException e) {
			Thread.currentThread().interrupt(); // stop thread
		} catch (Exception e) {
			throw new RuntimeException("Error proccessing frame", e); // send frame to the next effect
		}
	}
}
