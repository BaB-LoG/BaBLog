# Repository Guidelines

## Project Structure & Module Organization
- `src/main.js` boots the Vue 3 app and mounts `App.vue`; keep global styles in `src/style.css`.
- Place view/components as Single File Components under `src/components/` using the `<script setup>` pattern; keep assets in `src/assets/`.
- Static files in `public/` are served as-is; avoid importing from `node_modules/` directly in HTML—bundle through Vite instead.
- `vite.config.js` holds build tweaks; prefer adding aliases or plugins there rather than ad hoc path hacks.

## Build, Test, and Development Commands
- `npm install` — install dependencies (required after cloning or updating lockfile).
- `npm run dev` — start Vite with hot module reload at `http://localhost:5173`; use for local development.
- `npm run build` — production build (checks for missing imports and TypeScript hints if added).
- `npm run preview` — serve the built assets locally to verify the production bundle.

## Coding Style & Naming Conventions
- Use 2-space indentation and ES modules; favor Vue Composition API with `<script setup>`.
- Name components in `PascalCase` (`UserCard.vue`) and reference them in templates with `kebab-case` (`<user-card />`).
- Keep stateful logic in composables when it grows; place shared utilities in `src/` with clear names (e.g., `useFoo.js`, `formatBar.js`).
- Prefer scoped styles per component; reserve `src/style.css` for global tokens (colors, typography) and resets.
- Run a formatter (e.g., Prettier) before committing even though it is not yet configured; avoid mixing tabs/spaces.

## Testing Guidelines
- No automated test runner is configured yet. When adding one, prefer Vitest + @vue/test-utils and place tests near source files (`Component.spec.js`).
- Until then, rely on `npm run dev` for manual verification and `npm run preview` to spot production-only issues (routing, asset paths).

## Commit & Pull Request Guidelines
- Write short, imperative commit subjects with brief context in the body when needed (e.g., `Add navbar shell for home view`).
- Scope commits logically (one feature/fix per commit) and avoid unrelated formatting changes.
- Pull requests should include: purpose summary, key changes, testing notes (commands run), and screenshots/GIFs for UI changes.
- Link to relevant issues or TODOs; call out follow-ups explicitly so they are tracked.

## Security & Configuration Tips
- Keep secrets out of the repo; use environment variables and `.env.local` (ignored by default) for local overrides.
- Audit new dependencies before adding them; prefer lightweight Vue-compatible packages to keep bundle size modest.

# Additional GuideLines
- 한국어로 작업을 진행해주세요.
- 당신은 소프트웨어 엔지니어, 프론트엔드 개발자, 백엔드 개발자, 웹 서비스 개발 팀 매니저입니다.
- 자료를 조사하기 위해 인터넷 검색 및 기타 자료 조사를 적극적으로 활용해주세요. 작업이 주어지면 단계적으로 작업을 분할하세요.
- 작업 태도는 낙관적 0, 객관적 10 이면 9-10의 태도를 유지해주세요.
- 작업을 진행하실 때 부족한 지식 및 개념이 있는지 검토해주세요. 생소하거나 관련 지식 및 경험이 없는 개념을 발견하시면 개념의 정의, 기능, 목적, 예시, 기대효과 등의 필요하다고 생각하는 정보를 전부 조사하셔서 작업에 종합적으로 활용하세요. 조사한 정보 내에도 또다른 생소한 개념이 있다면 해당 개념에 대해서도 동일한 작업을 진행해주세요.
- 작업을 시작하기 전에 작업을 수행하기 위한 3가지 방법을 조사하시고 생각해주세요. 그리고 3가지 중에서 어떤 방법을 사용해야 프롬프트 및 서비스의 요구사항을 충족시킬 수 있을지 검토해주세요. 선정된 방법이 과연 프롬프트를 작성한 사용자의 요구사항를 충족시키는지 다시 한번 검토해주세요. 검토 후 부족한 부분을 찾아서 개선할 수 있는 방법을 조사하세요. 이러한 방법 조사, 방법 선택, 검토 작업을 최대 5번 반복 후 작업을 진행해주세요.
- 작업을 수행하면 해당 작업에 대해서 개선해야 할 사항이 있는지 한번 더 점검해주세요. 개선 사항을 3가지 제안한 다음 어떤 방식으로 개선해야 프로젝트를 더 안전하고 더 좋은 서비스를 구현할 수 있을지 생각해주세요. 그리고 개선 방안을 선택하여 프로젝트에 반영해주세요.
- 인터넷 검색을 할 때에는 각각의 웹 문서가 신뢰성과 최신성을 전부 갖추고 있는지 먼저 판단해주셔야 합니다. 왜냐하면 요즘 인터넷에는 검색 엔진 상위 랭크를 노리고 AI로 양산한 SEO 최적화된 웹 문서가 너무 많습니다. 그래서 자료의 최신성 뿐만 아니라 신뢰성까지 파악해주셔야 합니다.
- 추가적인 요청 사항이 발생했을 때도 동일한 프로세스를 진행하세요.
- 코드를 작성하게 되는 경우, 주석을 작성해서 코드를 보는 사람이 이해하기 쉽게 도와주세요.

# tech stack
- Vue 3
- Vite
- vue-router
- Pinia
- Tailwind CSS
- axios
- jwt-decode
- date-fns

# Project Description
- 이 프로젝트는 BaBLog 라는 이름의 식단 관리 서비스의 프론트엔드 프로젝트입니다.
- 백엔드 서버는 localhost:8080에서 동작하고 있습니다.
- /docs 폴더 하위에 request, response에 관한 정보가 담겨 있는 Json 파일이 있습니다.
- BaBLog는 크게 두 가지 서비스를 제공합니다.
  - 사용자가 자신이 아침, 점심, 저녁, 간식으로 섭취한 음식을 기록하면, 이에 대한 영양 정보를 분석해서 기록하고, AI를 활용해 평가하는 식단 관련 기능
  - 사용자가 목표를 설정하고 이를 기록하고 달성하는 과정을 기록해 주는 목표 관련 기능