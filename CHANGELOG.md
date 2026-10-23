# Changelog

## 1.0.0

First release, for Minecraft 1.21.1 and NeoForge 21.1.264 or newer. Client only.

### Window title

- Separate titles for the menus, singleplayer and multiplayer. An empty title uses the default one.
- Variables `{packname}`, `{packversion}`, `{mcversion}`, `{neoforge}`, `{modcount}`, `{username}`,
  `{modversion:<modid>}`, `{world}`, `{server}` and `{address}`.
- `{address}` stays empty unless `show_server_address` is enabled.
- Unknown variables stay in the title as written, with one warning in the log.

### Window icon

- Window icon from one PNG, scaled to 16, 24, 32, 48, 64, 128 and 256 pixels with clean transparent edges.
- Hand-drawn sizes such as `icon-16.png` replace the generated ones.
- The icon is set as soon as the game window is created. On Windows the taskbar button shows it even when the window
  has not been activated yet.
- A missing or invalid icon logs a warning and keeps the vanilla icon. macOS and Wayland are skipped.

### Title bar

- Light, dark or system title bar on Windows 10 1809 and newer.
- Title bar, title text and border colors on Windows 11.

### Window

- Taskbar flash when the game has loaded or a server disconnects you while the window is in the background.
- Initial window size, starting maximized, and remembering the window size, position and maximized state.

### Settings

- One config file, `config/customtitleicon-client.toml`, also editable in game under Mods, Custom Title & Icon,
  Config. Changes apply on save.
- English and Czech translations of the config screen.
