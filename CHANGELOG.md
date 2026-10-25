# RTE 更新日志

## 1.0.0-beta.3

### English

- Minecraft 1.20.4 builds are now released; 1.20.1 is still supported.
- Free nodes: non-integer positions, Y offset and wall snapping, moved and rotated with a gizmo.
- Standable rail collision boxes with multi-column layouts and script-defined layouts.
- MTR 4 coupled and mixed consists: each unit runs its own train script, so LCDs and car numbers stay correct per unit; no gangway between units that meet cab to cab.
- JS debug panel; texture downscaling options in the `O` settings; the resource workbench exports vehicle sounds and can merge several vehicles into one pack.
- ReplayMod replays show free nodes and the gizmo.
- Fixed memory leaks in long sessions; script work and LCD textures for a single-unit train drop to 1/2–1/3; a script call over 3 seconds is stopped and its file and line are logged.
- Fixed doors, door lights, gangways, floors and multi-locomotive cab parts in legacy packs.
- Fixed free node smoothing, one-way rail mirroring, manual Bézier defaults and route path angle checks; brush edits apply immediately.
- Fixed missing OBJ materials in workbench exports, decorations not rendering with BBMODEL preloading, and several disconnect and script errors.

### 中文

- 正式发布 Minecraft 1.20.4 版本，1.20.1 继续支持。
- 新增自由节点：非整数坐标、Y 轴位移、墙面吸附，用 Gizmo 拖动和旋转。
- 新增可站立的轨道碰撞箱，支持多列排布与 JS 自定义布局。
- 支持 MTR 4 列车重联与混编：每个车组单独运行脚本，LCD 与车号按车组正确显示；车头相对的车组之间不画连接处。
- 新增 JS 调试面板；「O」键设置可调贴图降采样；资源工作台可导出车辆音效，多辆车可合并导出为一个资源包。
- ReplayMod 回放中显示自由节点与 Gizmo。
- 修复长时间游玩的内存泄漏；单车组列车的脚本开销与 LCD 贴图数量降至 1/2～1/3；脚本单次运行超过 3 秒自动中止，并在日志中给出文件与行号。
- 修复旧版资源包列车的车门、门灯、连接处、地板，以及多机车连挂时的车头部件。
- 修复自由节点轨道平滑、单向轨道镜像、手动贝塞尔默认方向、线路路径创建器误报角度；刷子编辑立即生效。
- 修复资源工作台导出遗漏 OBJ 材质、开启 BBMODEL 预加载后装饰不渲染，以及多项断线和脚本报错。
