# Custom Title & Icon

<img src="src/main/resources/logo.png" alt="Custom Title & Icon logo" width="200">

A client-side NeoForge mod that sets the game window title, the window icon, the Windows title bar look and the
window size from one config file. Made for modpack authors, usable by anyone.

- Window title per game state (menu, singleplayer, multiplayer) with variables such as the pack version, world name
  or server name
- Window icon from one PNG, scaled to every size the system asks for, with optional hand-drawn small sizes
- Light, dark or custom colored title bar on Windows
- Taskbar flash when loading finishes or a server disconnects you
- Initial window size, starting maximized, and remembering size and position between starts

Requirements: Minecraft 1.21.1 and NeoForge 21.1.264 or newer. The mod is client only. Servers do not need it, and
players with the mod can join servers without it.

## Settings

All settings are in `config/customtitleicon-client.toml`. They can also be changed in game under Mods, Custom Title &
Icon, Config. Title, icon and title bar changes apply as soon as the file is saved, without a restart.

```toml
[pack]
    name = "Example Pack"
    version = "2.1.0"

[title]
    default = "{packname} {packversion}"
    singleplayer = "{packname} {packversion} - {world}"
    multiplayer = "{packname} {packversion} - {server}"
    show_server_address = false

[icon]
    file = "customtitleicon/icon.png"

[titlebar]
    theme = "system"
    caption_color = "#1E1E2E"
    text_color = "#CDD6F4"
    border_color = ""

[window]
    flash_when_loaded = true
    flash_on_disconnect = false
    initial_width = 1600
    initial_height = 900
    start_maximized = false
    remember_size = true
```

## Window title

| Key            | Used                                                     |
|----------------|----------------------------------------------------------|
| `default`      | In the menus, and for any state whose own title is empty |
| `singleplayer` | In a singleplayer world, including one opened to LAN     |
| `multiplayer`  | On a server, a Realm, or in another player's LAN world   |

The title changes when you join or leave a world or server. If `default` is empty, the vanilla title is kept.

| Variable               | Value                                                        |
|------------------------|--------------------------------------------------------------|
| `{packname}`           | `name` from the `[pack]` section                             |
| `{packversion}`        | `version` from the `[pack]` section                          |
| `{mcversion}`          | Minecraft version                                            |
| `{neoforge}`           | NeoForge version                                             |
| `{modcount}`           | Number of loaded mods, Minecraft and NeoForge included       |
| `{username}`           | Player name                                                  |
| `{modversion:<modid>}` | Version of the given mod, for example `{modversion:create}`  |
| `{world}`              | Name of the singleplayer world, empty elsewhere              |
| `{server}`             | Server name from the server list, empty elsewhere            |
| `{address}`            | Server address, only with `show_server_address = true`       |

`{address}` is empty unless `show_server_address = true`. It is off by default so that the address does not show up
on stream. An unknown variable stays in the title as written, and the log gets one warning about it.

## Window icon

### Preparing the icon

1. Make a square PNG with a transparent background, 256x256 or 512x512.
2. Save it as `config/customtitleicon/icon.png`. The mod creates the folder on first start.
3. Optionally, draw small sizes by hand and save them next to it as `icon-16.png`, `icon-24.png`, `icon-32.png` and
   so on. Each must be exactly that size.

The mod makes sizes 16, 24, 32, 48, 64, 128 and 256 from the icon, using a hand-drawn file where there is one, and
gives all of them to the window. Sizes larger than the source image are not generated. Transparent edges are scaled
without dark fringes.

On Windows, only two of the sizes are used: one close to the small icon size and one close to the large icon size at
the current display scale (16 and 32 at 100%, 24 and 48 at 150%, 32 and 64 at 200%). For a sharp hand-drawn title bar
icon at every display scale, provide `icon-16.png`, `icon-24.png` and `icon-32.png`.

### Behavior

The icon is set as soon as Minecraft creates its window, before mods finish loading. On Windows the taskbar button
shows it too, even when the window has not been activated yet.

If the file is missing, is not a PNG, or is not square, the log gets one warning and the vanilla icon stays. After
replacing the image file, save the config file or restart the game to see the new icon.

## Title bar (Windows)

`theme` sets whether the title bar is light or dark:

- `system` follows the Windows setting for apps (Settings, Personalization, Colors). If you change it while the game
  is running, the title bar follows the next time the game window gets focus.
- `light` and `dark` always use that look.

Windows draws a light title bar for Minecraft even in dark mode, so `system` is the default here.

On Windows 11, `caption_color`, `text_color` and `border_color` set the title bar background, the title text and the
window border as `#RRGGBB`. An empty value keeps the system color. A value in any other format is reset to empty, with
a warning in the log.

The look is applied when the window is created, after switching between fullscreen and windowed mode, and when the
config file is saved.

## Window

### Taskbar flash

- `flash_when_loaded` (on by default): when the game finishes loading while you are in another window, its taskbar
  button flashes.
- `flash_on_disconnect` (off by default): the same when a server kicks you or the connection is lost. A failed
  connection attempt counts too. Leaving a server yourself does not.

### Size and position

These settings take effect at the next start.

- `initial_width` and `initial_height` set the window size, centered on the main monitor. 0 keeps the default size.
- `start_maximized` starts the game with a maximized window.
- `remember_size` saves the window size, position and maximized state when the game closes and restores them at the
  next start. The state is saved in `config/customtitleicon/window-state.json`, so the config file does not change on
  every start. While a saved state exists, it is used instead of `initial_width`, `initial_height` and
  `start_maximized`.

A saved window is moved back onto a monitor if the monitor it was on is no longer connected, and made smaller if it
does not fit. When fullscreen is on in the video settings, the game starts in fullscreen and these settings are not
applied.

The NeoForge loading window shows up at its own size first. The size from this mod is applied when Minecraft takes
over that window, a few seconds later.

## Supported systems

| Feature          | Windows 11 | Windows 10 1809+ | Linux (X11) | Linux (Wayland) | macOS   |
|------------------|------------|------------------|-------------|-----------------|---------|
| Window title     | Yes        | Yes              | Yes         | Yes             | Yes     |
| Window icon      | Yes        | Yes              | Yes         | Ignored         | Ignored |
| Title bar theme  | Yes        | Yes              | Ignored     | Ignored         | Ignored |
| Title bar colors | Yes        | Ignored          | Ignored     | Ignored         | Ignored |

Taskbar flash, window size and position use the standard GLFW window functions, so on Linux and macOS they depend on
what the window manager allows. Settings that a system does not support are skipped without an error. The mod is
tested on Windows 11.

## For modpack authors

- Ship `config/customtitleicon-client.toml` and the `config/customtitleicon/` folder with your icon in the pack.
- Keep `remember_size` on if players should keep their own window size, or off if every start should use
  `initial_width` and `initial_height`.
- Ship `config/fml.toml` with the NeoForge loading window set to the same size as `initial_width` and
  `initial_height`, so the window does not change size during startup:

  ```toml
  earlyWindowWidth = 1600
  earlyWindowHeight = 900
  earlyWindowMaximized = false
  ```

  A launcher that passes its own window size (`--width` and `--height`) overwrites `earlyWindowWidth` and
  `earlyWindowHeight` on every start.

## Building

```
./gradlew build
```

The mod jar is written to `build/libs`. `./gradlew runClient` starts a development client.

## License

MIT, see [LICENSE](LICENSE).
