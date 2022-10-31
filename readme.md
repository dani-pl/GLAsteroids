Author: Daniel Peña López
Date: October 2022

** G improvements **
1. Sound effects and Background music
   
2. Implement an FPS counter (milliseconds-per-frame to the player)
   
5. Create a HUD to display the current level, remaining lifes and the score 
   
6. Have 3 distinct asteroids; a small, medium and large. Smaller asteroids are worth more points 
   and move faster. Note: only properties change here, not behavior. So all three asteroids should 
   be implemented as a single class. Let the constructor take the type: "small", "medium", "large" 
   and figure it out from there.

7. Make asteroids break apart when hit. Big asteroids break into smaller asteroids that move faster.
10. Load the shader code from the filesystem

** VG improvement **
2. Implement levels (or "waves"). When the player destroys all asteroids the level increases 
   and new asteroids spawn. Each level (or wave) should be more challenging (and more rewarding)
   than the last.