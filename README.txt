If you're reading this, that means you've maybe decided to help with Newtonian! Gee, Thanks! 

At it's current level, this tool only writes Box2D static objects, not kinematic and not dynamic. Yeah, it's a drag, but static objects are what you'll be drawing most of anyway. This tool is intended to be used with the LibGDX game making structure, but I guess it could be re-purposed easily enough.

For it to work to it's fullest, you MUST insert the three tags in this order (as comments):
#body-defs			inserts class variable declarations, put at beginning of class
#element-spawn-methods		inserts bulder method calls, put in the 'create' method
#method-declarations		writes the actual builder methods, put anywhere you could declare a method



Some miscelaneous notes:
-polygons are limited to 8 vertices (it's a libgdx thing)
-libgdx only allows convex polygon. Convex means rounded outwards. e.g. full moon=convex, half moon=convex, crescent moon!=convex. No sweat, though! It will automatically make polygons convex if they're not already. It's pretty robust.
-if you try to break it, you'll probably be able to. No guarantees ^__^
-it writes to and renames files, so be careful using editors that put a lock on files. I know Eclipse, gedit, and vim are safe.
-if you can figure out how to avoid spawning hundreds of rectangles to fill the grid, please do! It makes startup slow.
-if you can figure out how to avoid making so many temp File objects when changing the file name, please do!
-actually, if you can in any way make something better, i would totally appreciate it. it's a public repo, just don't change the master please! Make a branch.

