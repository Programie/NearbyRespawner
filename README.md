# NearbyRespawner

A Minecraft Bukkit plugin which respawns players near the death location on death.

<img src="logo.png" alt="Logo" height="300"/>

[![pipeline status](https://gitlab.com/Programie/NearbyRespawner/badges/master/pipeline.svg)](https://gitlab.com/Programie/NearbyRespawner/commits/master)
[![download latest release](https://img.shields.io/badge/download-latest-blue.svg)](https://gitlab.com/Programie/NearbyRespawner/-/jobs/artifacts/master/raw/target/NearbyRespawner.jar?job=release)

## What is it?

Have you ever moved away a large distance from your world spawn or spawn bed just to get killed by those annoying creepers and finding yourself respawning too far away from where you died? Then this Bukkit plugin has just been made for you!

This plugin tries to find a safe location around your latest death location and respawns you at the new safe location. As getting respawned next to the death location would be too easy, the plugin searches for a random location in a configurable radius around the death location (default: max 1000 blocks).

Searching for a safe location is done by checking the highest block of a random location. So you won't be randomly spawned unarmed in a cave next to a cave spider spawner. The plugin also tries to prevent respawning yourself in lava lakes and big oceans.

In case your normal respawn location (e.g. your spawn bed or world spawn) would be near your death location (default: max 1000 blocks), the plugin simply uses the normal respawn behavior letting you respawn in your safe house or world spawn.

## Installation

You can get the latest release from [GitLab](https://gitlab.com/Programie/NearbyRespawner/-/jobs/artifacts/master/raw/target/NearbyRespawner.jar?job=release).

## Configuration

The plugin allows to change some values described in the [config.yml provided with this plugin](src/main/resources/config.yml). Load the plugin once to generate it.

## Commands

Currently, there are no commands available.

## Permissions

* `nearbyrespawner.random-respawn`: Allow players to get respawned at a random location (default: `true`)

## Build

You can build the project in the following 2 steps:

 * Check out the repository
 * Build the jar file using maven: *mvn clean package*

**Note:** JDK 1.8 and Maven is required to build the project!