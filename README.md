# JDA-Command-Manager
This is a library for creating complete command systems with JDA driven Discord bots.

# Getting Started

To add this to your project, simply navigate to the `builds` directory and select the latest JAR. Download it and import it into your Java project that uses JDA to make a Discord bot.

If you're using Gradle in IntelliJ like me, you just need to put the jar in a `libs` directory in the root of your project and build Gradle.

In theory this project could be used with something like jitpack to build it straight from Gradle automatically, but I have been entirely unable to get that to work. I'd love if someone showed me a functional build using jitpack that I could coppy.

# Making a Bot

I'll write instructions to build a bot when I have time. Here's the gist of it thought:

• Each "command" that your bot supports is controlled by a Json file. You can build these files using the various Builder classes in the library and then modify them more precisely in a text editor later.
• A "command manager" controls a set of commands, usually a folder with Json files in it. Most projects will only have one Command Manager, but you might find it useful to have multiple, such as for separating admin commands and everyone else commands.
• Normally you're probably making the main JDA instance with a JDABuilder in your main class. Right after doing that, you should also make a CommandManager. You can do that with `CommandUtils.createDefaultManager().build()`. Point it to the folder with all your Json files.
• Each Json file will instantiate either a CallResponseCommand or a Function. The former are realy basic commands that send something in Discord that you hard coded in the Json. Functions are more complicated. They take arguments based on one or more syntaxes, parse and validate them, and call a custom method.

If that's still confusing, you could try reading some of the Javadocs. I write a lot of documentation. Or you could wait until I get around to writing a better readme. :/

# Updates

If this project hasn't been updated in a month, I'm probably still going to come back to it. I intend to use this library in all my future Discord bots, and I'll add features as I need them myself. If this project hasn't been updated in sixth months, I might just not be making any major Discord bots. If this project hasn't been updated in a year, it's probably out of date from JDA and you might not want to use it. If this project hasn't been updated in ten years, I definitely don't care about it anymore and you shouldn't either. If this project hasn't been updated in a hundred years, (a) I'm shocked you're reading this still, (b) does Github seriously still exist, (c) how has Discord survived this long?, and (d) this will not be receiving any more updates because I am dead.
