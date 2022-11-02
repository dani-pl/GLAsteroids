Author: Daniel Peña López

Date: October 2022

**G improvements**
-  *G improvement 1:* Sound effects and Background music
   
   *Implementation:* I have a total of 6 sound effects and 3 background musics.
   The sound effects are: boost, death, explosion, shoot, starting, win. On the other hand, I have
   three background musics: one for odd levels and another one for even levels. The third is used
   when the saucer enemy appears. Then the background music of the level is replaced and the one of
   the saucer enemy starts playing until the enemy is killed.
   
-  *G improvement 2:* Implement an FPS counter (milliseconds-per-frame to the player): This counter appears on the
   bottom of the screen and it shows the average milliseconds-per-frame during the last 10 frames.

   *Implementation:* To implement this I have used a couple of variables, one which counts 10 frames (frameCounter) 
   and another one that stores the accumulated time during these frames (accumulator2). Then, its
   calculation is accumulator2/frameCounter. I have chosen to do it each 10 frames because
   otherwise, the number changes too quickly to even appreciate a single update. To display it on the
   screen I use a Render HUD which I explain in the next improvement.
   
-  *G improvement 5:* Create a HUD to display the current level, remaining lives and the score.

   *Implementation:* The Render HUD class shows all this information on the top of the screen. I have also included methods for the fps 
   counter and the closing waves texts (game over or level successful).
   
-  *G improvement 6:* Have 3 distinct asteroids; a small, medium and large. Smaller asteroids are worth more points 
   and move faster. Note: only properties change here, not behavior. So all three asteroids should 
   be implemented as a single class. Let the constructor take the type: "small", "medium", "large" 
   and figure it out from there.

   *Implementation:* As it was suggested in the description of the improvement, I have included in the Asteroid 
   constructor a type variable. If this variable is omitted, a random type will be assigned in
   the init section, otherwise it keeps the defined type. This is useful because we want random
   asteroids when a wave start, but we will want specific types when an asteroid is destroyed and
   it breaks apart in other asteroids.
   To define the type of the asteroids, I have used an enum.
   It is worth to mention that the smaller the asteroid is (in terms of width), the more points the
   player receives. The amount of points is also proportional to the wave in which the player is.

-  *G improvement 7:* Make asteroids break apart when hit. Big asteroids break into smaller asteroids that move faster.

   *Implementation:* In my implementation big asteroids become some medium asteroids and medium asteroid become some
   small asteroids. Small asteroids do not become anything else when they are approached by a bullet.
   
-  *G improvement 10:* Load the shader code from the filesystem.

   *Implementation:* I access two text files with the assetsManager in which
   I have stored the code for both the vertexShaderCode and the FragmentShaderCode. Then, I read the
   content with a BufferedReader.

**VG improvements**
-  *VG improvement 1:* Add the flying saucer enemy from the original game. It appears randomly, flies across the screen
   and shoots towards the player.

   *Implementation:* In my implementation, the saucer enemy moves with a constant value
   that is assigned in the init section (similar to an asteroid). It has the same shape as a player
   but the enemy is red. The enemy rotates constantly to be pointing towards the player. I made the
   saucer enemy start with a bulletCoolDown higher than zero to give the player some time to see that
   the saucer enemy has appeared and it is going to start to shoot (otherwise the saucer enemy kills
   player very quickly).
   You can see that more than one life can be lost when a bullet from the enemy approaches the
   player, this happens because more than one update() occurs while the bullet is inside the player.
   I considered to change this behaviour and force to lose the player to lose just one life, but I 
   found more realistic to lose a number of lifes proportional to the time the bullet has been inside
   the player.
   On the other hand, I have decided that the bullets of the enemy cannot destroy asteroids.
   Moreover, you will see that a bullet that leaves the world space does not reappear in the world
   space, I find it more realistic this way too.
   Lastly, this saucer enemy appears randomly, every update I generate a random number in a given
   interval and I check if that number is equal to zero, in case it is the saucer enemy appears.
   If you kill it, you get a big fixed number of points that also depends on the wave you are.
   
-  *VG improvement 2:* Implement levels (or "waves"). When the player destroys all asteroids the level increases 
   and new asteroids spawn. Each level (or wave) should be more challenging (and more rewarding)
   than the last.

   *Implementation:* In each wave, I increment the number of initial asteroids and the number of points for killing
   an asteroid or saucer enemy is proportional to the wave number.
   
-  *VG improvement 3:* Create a Shader-class that can load GLSL-source code from the filesystem, build a shader 
   program and provide an interface for tweaking all of the shader settings, and 
   enabling / disabling the shader. (now you can give each Entity their own Shader
   if you'd like!).

   *Implementation:* In my implementation, I have converted the GLManager into a class and I have created some
   interfaces that allow me to change the PointSize, LineWidth, ShaderColor. And two interfaces to
   enableShader() and disableShader().