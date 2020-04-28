AdjustingSpawnLimit
===================

This is a plugin for Spigot/Paper servers that have lag issues due to to many
hostile mobs in the world.

The bukkit.yml has a setting (spawn-limits.monsters) do set the number of mobs
that can spawn, but this setting may be too high when many players are online,
causing lag, and too low with only one or two players, causing mob farms to
be inefficient.

This plugin monitors tps (transactions per seconds; a healthy server should
have 20.0), and turns down the mob spawn limit when tps drop, and
turns the limit up again when tps become better. It does this in slight
increments/decrements, to avoid temporary load spikes affecting the mob count
too much.

Installing
----------
Just copy the .jar file into the plugins folder and start the server. The
plugin will create a default config, that should be ok for most servers, which
you can edit afterwards.

Config options
--------------
* *decreaseUnderTPS* - every time the plugin checks tps, if the actual value
   is below this, the mob count will be decreased.
* *increaseOverTPS* - every time the plugin checks tps, if the actual value is
   above this, the mob count will be increased
* *minimumPercent* - The minimum percentage of the original value that the
   plugin will decrease to. No matter how slow the server becomes, you'll
   always get at lest this amount of mobs. Remember this is in percent, not
   the absolute value. If you have this value at 50, and your bukkit.yml
   has spawn-limits.monsters at 70, the plugin will not adjust below 35 mobs.
* *stepsize* The value that the percentage will decrease/increase when the
   plugin decides to adjust. So when the current mob count is at 90%, with this
   value at 5, you'll get 95% next when TPS is better than increaseOverTPS,
   or 85% next when TPS is worse than decreaseUnderTPS.
   

Configuring
-----------
It is very rare to get an exact 20.0 tps, as a single glitch will lower tps,
and as the plugin checks the average once a minute, you'll have a lower value
every time anything happens in that minute. So you should only decrease your
mob count if tps is more than just slightly below 20, and you should increase
it even a bit below 20. This is why the default config has 19 for the
decrease margin, and 19.5 for the increase margin.

As, in the default, the minimum percent is 50, and the step size is 5, and
the plugins adjusts the value once per minute, a server that has a sudden but
long lasting tps drop will take 10 minutes to set the mob cap to 50% its
original. You can increase the step size to adjust the cap faster. But remember
there's a lot of reasons for low tps that have nothing to with mobs, and many
of those reasons exist for a short time only, so don't set step size
too high! If you do, players will experience great differences in 
the efficiency of mob farms, especially non-spawner-based ones, in short time
periods.
