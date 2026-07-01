# Frontend Refactor Prompt — Service Registry UI
# Role: Staff Frontend Engineer
# Stack: TypeScript · React · (CSS Modules or Tailwind — specify yours)

---

You are a staff frontend engineer performing a comprehensive refactor of a
TypeScript + React application: a **Service Registry** tool used by platform
and product engineers to visualise and manage service dependencies, business
flows, and API contracts.

The existing codebase was built iteratively and has accumulated inconsistencies
across design tokens, component APIs, scrollbar behaviour, font sizing, input
field patterns, and reusability. Your job is to refactor it so that the next
engineer to open any file finds a codebase that is consistent, extensible,
and pleasant to work in.

Work through every section below. Treat each as a required deliverable, not
a suggestion. Where you produce code, make it production-ready TypeScript —
no `any`, no inline style objects in JSX unless they are the result of a
design-token lookup, no magic numbers.

---

## SECTION 1 — Design Token System

### 1.1  Audit and consolidate the existing token set

The current codebase uses CSS custom properties defined inconsistently across
multiple files. Consolidate them into a single source of truth.

Produce:

```
src/tokens/
  colors.ts          — all color values as a typed const object
  typography.ts      — font families, sizes, weights, line-heights
  spacing.ts         — spacing scale (4px base)
  radii.ts           — border-radius values
  shadows.ts         — box-shadow values
  borders.ts         — border widths and colors
  motion.ts          — transition durations and easing functions
  index.ts           — re-exports everything; this is the only import consumers need
```

The current token names in use (taken from the existing codebase) must be
preserved as aliases during migration so no component breaks before it is
updated. Map each existing name to its new canonical token:

| Existing CSS variable             | New token path                         |
|-----------------------------------|----------------------------------------|
| --color-background-primary        | tokens.colors.surface[0]               |
| --color-background-secondary      | tokens.colors.surface[1]               |
| --color-background-tertiary       | tokens.colors.surface[2]               |
| --color-border-tertiary           | tokens.colors.border.subtle            |
| --color-border-secondary          | tokens.colors.border.default           |
| --color-text-primary              | tokens.colors.text.primary             |
| --color-text-secondary            | tokens.colors.text.secondary           |
| --color-text-tertiary             | tokens.colors.text.muted               |
| --color-text-info                 | tokens.colors.text.info                |
| --color-text-success              | tokens.colors.text.success             |
| --color-text-warning              | tokens.colors.text.warning             |
| --border-radius-lg                | tokens.radii.lg  (12px)                |
| --border-radius-md                | tokens.radii.md  (6px)                 |
| --font-sans                       | tokens.typography.fontFamily.sans      |

### 1.2  Semantic colour palette

The existing app uses the following raw hex values scattered inline across
stylesheets. Extract them into a semantic palette with intent-based names.
The full palette currently in use:

```
/* Purple scale (primary interactive) */
#EEEDFE  #AFA9EC  #7F77DD  #534AB7  #3C3489  #26215C

/* Teal scale (success / healthy / gateway) */
#E1F5EE  #1D9E75  #0F6E56  #085041

/* Amber scale (warning / message broker / async) */
#FAEEDA  #EF9F27  #854F0B  #633806

/* Red scale (error / database / critical) */
#FCEBEB  #E24B4A  #A32D2D  #501313

/* Blue scale (info / gRPC) */
#E6F1FB  #378ADD  #185FA5  #0C447C

/* Green scale (SLO met / confirmed) */
#EAF3DE  #97C459  #3B6D11  #27500A

/* Neutral scale */
#F1EFE8  #D3D1C7  #888780  #5F5E5A  #1A1A18

/* Capability / planning */
#EEF0FF  #AFA9EC  #4551B5
```

Each must map to a semantic role:

```typescript
// Example shape — expand all scales fully
export const colors = {
  surface: {
    0: '#FFFFFF',     // primary background
    1: '#F5F4F1',     // secondary / sidebars
    2: '#EDECEA',     // tertiary / canvas
    page: '#D8D6CF',  // page background (outside app shell)
  },
  border: {
    subtle:  '#E0DED8',
    default: '#CCCAC3',
    strong:  '#B4B2A9',
  },
  text: {
    primary:   '#1A1A18',
    secondary: '#5F5E5A',
    muted:     '#888780',
    info:      '#0C447C',
    success:   '#27500A',
    warning:   '#633806',
    danger:    '#A32D2D',
    accent:    '#534AB7',
  },
  intent: {
    // Background + foreground + border for status badges/chips
    healthy:  { bg: '#EAF3DE', text: '#27500A', border: '#97C459' },
    warning:  { bg: '#FAEEDA', text: '#633806', border: '#EF9F27' },
    danger:   { bg: '#FCEBEB', text: '#A32D2D', border: '#E24B4A' },
    info:     { bg: '#E6F1FB', text: '#0C447C', border: '#85B7EB' },
    accent:   { bg: '#EEEDFE', text: '#3C3489', border: '#AFA9EC' },
    neutral:  { bg: '#F5F4F1', text: '#5F5E5A', border: '#CCCAC3' },
  },
  nodeKind: {
    // Per-node-kind icon background + icon color
    SERVICE:         { bg: '#EEEDFE', icon: '#534AB7' },
    API_GATEWAY:     { bg: '#E1F5EE', icon: '#0F6E56' },
    MESSAGE_BROKER:  { bg: '#FAEEDA', icon: '#854F0B' },
    DATABASE:        { bg: '#FCEBEB', icon: '#A32D2D' },
    EXTERNAL_SYSTEM: { bg: '#F1EFE8', icon: '#5F5E5A' },
  },
  protocol: {
    REST:      { bg: '#EEEDFE', text: '#3C3489' },
    GRPC:      { bg: '#E6F1FB', text: '#0C447C' },
    ASB:       { bg: '#FAEEDA', text: '#633806' },
    JDBC:      { bg: '#FCEBEB', text: '#A32D2D' },
    WEBSOCKET: { bg: '#EAF3DE', text: '#27500A' },
  },
} as const;
```

### 1.3  Typography scale

The existing app uses raw `font-size` values: 9px, 10px, 11px, 12px, 13px,
14px, 16px, 18px, 20px, 22px, 38px. Map them to a named scale:

```typescript
export const typography = {
  fontFamily: {
    sans: "-apple-system, BlinkMacSystemFont, 'Segoe UI', system-ui, sans-serif",
    mono: "'SF Mono', 'Consolas', 'Menlo', monospace",
  },
  fontSize: {
    '2xs': '9px',
    xs:    '10px',
    sm:    '11px',
    md:    '12px',
    base:  '13px',
    lg:    '14px',
    xl:    '16px',
    '2xl': '18px',
    '3xl': '20px',
    '4xl': '22px',
    display: '38px',
  },
  fontWeight: {
    regular: 400,
    medium:  500,
    semibold: 600,
    bold:    700,
  },
  lineHeight: {
    tight:  1.25,
    normal: 1.5,
    loose:  1.7,
  },
  letterSpacing: {
    tight:   '-0.01em',
    normal:  '0em',
    wide:    '0.04em',
    widest:  '0.06em',
  },
} as const;
```

### 1.4  Spacing scale

All padding, margin, and gap values must come from a 4px base-unit scale.
Replace all inline magic numbers:

```typescript
export const spacing = {
  0:  '0px',
  px: '1px',
  1:  '4px',
  2:  '8px',
  3:  '12px',
  4:  '16px',
  5:  '20px',
  6:  '24px',
  8:  '32px',
  10: '40px',
  12: '48px',
} as const;
```

---

## SECTION 2 — Component Architecture

### 2.1  Component inventory

Audit the codebase and produce a complete inventory of every UI pattern
currently rendered, grouped by abstraction level:

**Primitives** (no business logic, no data fetching):
- `Text` — renders a `<span>` or `<p>` with a `size`, `weight`, `color` prop
  that resolves from the typography and color tokens. Never accepts a raw
  color string — only token keys.
- `Icon` — thin wrapper around the Tabler icon library. Accepts `name`,
  `size`, `color` (token key only), `aria-label`.
- `Badge` / `Chip` — status badge that accepts an `intent` prop
  (`healthy | warning | danger | info | accent | neutral`) and derives
  background, text, and border color from `tokens.colors.intent[intent]`.
  Must not accept raw color props.
- `Dot` — colored dot used in filter lists. Accepts `nodeKind` or `color`
  (token key only).
- `Divider` — horizontal rule, styled from border tokens.
- `Avatar` — team initial circle, accepts `initials`, `bg`, `size`.

**Inputs** (see Section 3 for full input spec):
- `TextInput`, `SearchInput`, `Select`, `Checkbox`, `RadioGroup`

**Surfaces**:
- `Card` — padded container with border-radius and optional border.
  Accepts `padding`, `radius`, `border` (all from tokens).
- `Panel` — the right-hand detail panel shell: header + scrollable body.
- `Sidebar` — left-side navigation shell.
- `Modal` / `Dialog` — accessible modal with focus trap.
- `Tooltip` — hover tooltip, `position` prop.
- `Popover` — anchored popover for contextual info.

**Compound / domain components**:
- `NodeIcon` — icon + background derived from `nodeKind` token.
- `StatusBadge` — chip pre-wired to `HealthStatus` or `LifecycleStage` enum.
- `ConnectionTypeTag` — protocol badge (REST / gRPC / ASB / JDBC …).
- `MetricCard` — value + label card from the metrics grid.
- `StepCard` — a single step row in a flow detail panel.
- `SectionLabel` — uppercase label above a detail group.
- `KeyValueRow` — label + value row in metadata sections.
- `ConnectionRow` — icon + name + meta + type tag used in "Nodes in flow" lists.
- `SloChip` — SLO status chip with automatic color from status.
- `BreadcrumbBar` — domain › capability › flow breadcrumb.
- `VariantChip` — flow variant selector chip.

### 2.2  Component API rules (apply to every component)

**Props must be:**
- Typed with a named interface, not an inline type literal.
- Documented with JSDoc on every prop.
- Optional where a sensible default exists; defaults declared with
  `defaultProps` or destructuring defaults, never conditional logic in render.
- Extended from the relevant HTML element's props via
  `React.ComponentPropsWithoutRef<'div'>` or equivalent, so `className`,
  `data-*`, and `aria-*` props pass through automatically.

```typescript
// ✅ Correct pattern
interface BadgeProps extends React.ComponentPropsWithoutRef<'span'> {
  /** Visual intent — drives background, text, and border color */
  intent: IntentKey;
  /** Optional leading icon name from Tabler Icons */
  icon?: string;
  /** Text label */
  children: React.ReactNode;
}

// ❌ Wrong — raw color, no passthrough, no docs
const Badge = ({ color, text }: { color: string; text: string }) => ...
```

**Composition over configuration:**
Components must be composable via `children`, slots, or render props rather
than growing a config prop for every new use case. A `Panel` component should
not have 15 boolean flags — it should expose `<Panel.Header>`,
`<Panel.Body>`, `<Panel.Footer>` sub-components.

```typescript
// ✅ Slot pattern
<Panel>
  <Panel.Header>
    <NodeIcon kind="SERVICE" />
    <Panel.Title>Order Service</Panel.Title>
  </Panel.Header>
  <Panel.Body>
    <SectionLabel>Ownership</SectionLabel>
    <KeyValueRow label="Team" value="Orders Squad" />
  </Panel.Body>
</Panel>

// ❌ Config prop explosion
<Panel
  iconKind="SERVICE"
  title="Order Service"
  sectionLabel="Ownership"
  teamLabel="Team"
  teamValue="Orders Squad"
/>
```

**Variant props for visual state, not boolean flags:**
```typescript
// ✅
<StepCard variant="critical" />
<StepCard variant="async" />
<StepCard variant="optional" />

// ❌
<StepCard isCritical isAsync={false} isOptional />
```

**Never accept raw color strings:**
Any component that renders a color must accept a token key, an intent key,
or a `nodeKind` key — never a raw hex string. This ensures dark mode can
be added later by swapping the token values without touching components.

### 2.3  Folder structure

```
src/
  components/
    primitives/
      Text/
        Text.tsx
        Text.stories.tsx   ← Storybook story
        Text.test.tsx
        index.ts
      Badge/
      Icon/
      Dot/
      Divider/
      Avatar/
    inputs/
      TextInput/
      SearchInput/
      Select/
      Checkbox/
      RadioGroup/
    surfaces/
      Card/
      Panel/
      Sidebar/
      Modal/
      Tooltip/
      Popover/
    domain/
      NodeIcon/
      StatusBadge/
      ConnectionTypeTag/
      MetricCard/
      StepCard/
      SectionLabel/
      KeyValueRow/
      ConnectionRow/
      SloChip/
      BreadcrumbBar/
      VariantChip/
  tokens/
    colors.ts
    typography.ts
    spacing.ts
    radii.ts
    shadows.ts
    borders.ts
    motion.ts
    index.ts
  hooks/
    useKeyboard.ts       ← keyboard navigation helper
    useScrollLock.ts     ← lock body scroll when modal is open
    useResizeObserver.ts ← for dynamic panel heights
    useClickOutside.ts   ← for popover / dropdown dismiss
  utils/
    cn.ts               ← classnames utility (clsx or tailwind-merge)
    tokenHelpers.ts     ← resolve token key → CSS value at runtime
```

---

## SECTION 3 — Input Component Specification

Every input field in the application must use the same base `TextInput`
component. The existing codebase has at least 4 different implementations
of a search box (`.search-box input`, `.event-input-wrap input`, etc.) —
consolidate them all into one.

### 3.1  TextInput

```typescript
interface TextInputProps extends React.ComponentPropsWithoutRef<'input'> {
  /** Label text — always required for accessibility; set hideLabel to show only visually */
  label: string;
  /** Hides label visually but keeps it in the DOM for screen readers */
  hideLabel?: boolean;
  /** Optional leading icon (Tabler icon name) */
  leadingIcon?: string;
  /** Optional trailing icon or action button */
  trailingElement?: React.ReactNode;
  /** Contextual hint below the field */
  hint?: string;
  /** Inline error message — also sets aria-invalid and aria-describedby */
  error?: string;
  /** Input size variant */
  size?: 'sm' | 'md' | 'lg';
  /** Full width of its container */
  fullWidth?: boolean;
}
```

**Visual spec** (from existing design system):
- Border: `0.5px solid tokens.colors.border.default`
- Border-radius: `tokens.radii.md` (6px)
- Background: `tokens.colors.surface[1]`
- Font: `tokens.typography.fontSize.md` (12px), `tokens.typography.fontFamily.sans`
- Text color: `tokens.colors.text.primary`
- Placeholder color: `tokens.colors.text.muted`
- Focus ring: `2px solid tokens.colors.accent[400]` (i.e. `#7F77DD`), offset 1px
- Error state: border `tokens.colors.border.danger`, background tint
- Disabled: `opacity: 0.5`, `cursor: not-allowed`
- Leading icon: positioned `left: 8px`, `top: 50%`, `transform: translateY(-50%)`
  using `tokens.colors.text.muted` at 14px

**Sizes:**

| size | height | padding-x | font-size |
|------|--------|-----------|-----------|
| sm   | 28px   | 8px       | 11px      |
| md   | 32px   | 8px       | 12px      |
| lg   | 36px   | 10px      | 13px      |

**States to implement:**
- Default, hover (border darkens to `border.strong`), focus (ring),
  filled, disabled, error, read-only.

### 3.2  SearchInput

A specialisation of `TextInput` with a search icon locked to leading position
and an optional clear button that appears when the field has a value.

```typescript
interface SearchInputProps
  extends Omit<TextInputProps, 'leadingIcon' | 'label'> {
  /** Accessible label (default: "Search") */
  label?: string;
  /** Called when clear button is pressed */
  onClear?: () => void;
  /** Debounce delay in ms (default: 0 — consumer controls debounce) */
  debounceMs?: number;
}
```

### 3.3  Select

Must not use the native `<select>` element for styled contexts — implement
a custom listbox following ARIA `role="listbox"` pattern with keyboard
navigation (arrow keys, Home, End, Escape, type-ahead).

```typescript
interface SelectOption<T extends string = string> {
  value:    T;
  label:    string;
  disabled?: boolean;
  icon?:    string;   // Tabler icon name
}

interface SelectProps<T extends string = string> {
  label:       string;
  options:     SelectOption<T>[];
  value?:      T;
  defaultValue?: T;
  onChange?:   (value: T) => void;
  placeholder?: string;
  disabled?:   boolean;
  error?:      string;
  size?:       'sm' | 'md' | 'lg';
  fullWidth?:  boolean;
}
```

---

## SECTION 4 — Scrollbar Specification

The existing app clips content in panel bodies, sidebar lists, and canvas
areas. Implement consistent, cross-browser scrollbar styling.

### 4.1  Scrollbar design tokens

```typescript
export const scrollbar = {
  width:         '6px',
  trackBg:       'transparent',
  thumbBg:       tokens.colors.border.default,      // #CCCAC3
  thumbBgHover:  tokens.colors.border.strong,       // #B4B2A9
  thumbRadius:   '3px',
};
```

### 4.2  Global scrollbar CSS mixin

Define once in a global stylesheet or CSS-in-JS shared mixin — never repeat
inline:

```css
/* Applied to every scrollable container via a shared .scrollable class
   or a CSS-in-JS mixin function */
.scrollable {
  overflow-y: auto;
  overflow-x: hidden;
  scroll-behavior: smooth;

  /* WebKit (Chrome, Safari, Edge) */
  &::-webkit-scrollbar {
    width: 6px;
  }
  &::-webkit-scrollbar-track {
    background: transparent;
  }
  &::-webkit-scrollbar-thumb {
    background-color: #CCCAC3;
    border-radius: 3px;
    transition: background-color 150ms ease;
  }
  &::-webkit-scrollbar-thumb:hover {
    background-color: #B4B2A9;
  }
  /* Firefox */
  scrollbar-width: thin;
  scrollbar-color: #CCCAC3 transparent;
}

/* Only show scrollbar on hover for non-touch contexts */
@media (hover: hover) {
  .scrollable-hover {
    &::-webkit-scrollbar-thumb {
      background-color: transparent;
    }
    &:hover::-webkit-scrollbar-thumb {
      background-color: #CCCAC3;
    }
  }
}
```

### 4.3  Where to apply scrollable behaviour

| Container                    | Class / variant               | Axis     |
|------------------------------|-------------------------------|----------|
| Sidebar tree / flow list     | `scrollable-hover`            | vertical |
| Right panel body             | `scrollable`                  | vertical |
| Sequence diagram             | `scrollable`                  | both     |
| Graph canvas                 | overflow-hidden (pan in JS)   | —        |
| Modal body                   | `scrollable`                  | vertical |
| Dropdown listbox             | `scrollable` max-height 240px | vertical |
| Metrics grid (mobile)        | `scrollable`                  | horizontal |

### 4.4  Scroll preservation

When a user selects a different flow in the sidebar, the right panel body
must scroll back to top. Implement a `useScrollReset` hook:

```typescript
function useScrollReset(ref: React.RefObject<HTMLElement>, dep: unknown) {
  useEffect(() => {
    ref.current?.scrollTo({ top: 0, behavior: 'instant' });
  }, [dep]); // dep is typically the selected flowId
}
```

---

## SECTION 5 — Typography Enforcement

### 5.1  The `Text` component is mandatory

Every piece of rendered text must use the `Text` primitive. No raw `<p>`,
`<span>`, `<h1>`–`<h6>`, or `<label>` elements in feature components.
The `Text` component maps semantic roles to font-size + weight + color:

```typescript
type TextVariant =
  | 'label-uppercase'   // 10px, 500, muted, uppercase, 0.05em tracking
  | 'caption'           // 9px,  400, muted
  | 'body-sm'           // 11px, 400, secondary
  | 'body'              // 12px, 400, secondary
  | 'body-md'           // 13px, 400, primary
  | 'label'             // 12px, 500, primary
  | 'heading-sm'        // 13px, 500, primary
  | 'heading'           // 14px, 500, primary
  | 'metric'            // 18px, 500, primary
  | 'display'           // 22px, 500, primary
  | 'mono'              // 12px, 400, accent, monospace
  | 'mono-sm'           // 10px, 400, accent, monospace
  ;

interface TextProps extends React.ComponentPropsWithoutRef<'span'> {
  variant:   TextVariant;
  /** Override the resolved color with a token key */
  color?:    TextColorKey;
  /** Truncate with ellipsis when content overflows */
  truncate?: boolean;
  /** Render as a different HTML element */
  as?: React.ElementType;
}
```

### 5.2  Section labels

The pattern `font-size: 11px; font-weight: 500; text-transform: uppercase;
letter-spacing: 0.04–0.05em; color: muted` appears in at least 20 places
in the existing codebase. Replace every occurrence with:

```tsx
<SectionLabel>Ownership</SectionLabel>
// which renders: <Text variant="label-uppercase">Ownership</Text>
```

### 5.3  Monospace fields

All code paths, endpoint paths, SQL snippets, proto methods, and entity IDs
must use `<Text variant="mono">` or `<Text variant="mono-sm">`, never a raw
`<code>` or inline `font-family`.

---

## SECTION 6 — Layout & Shell Refactor

### 6.1  App shell

The three-column grid (`sidebar | main | detail`) is the root layout and
must be implemented as a single `AppShell` component:

```typescript
interface AppShellProps {
  sidebar:    React.ReactNode;
  main:       React.ReactNode;
  detail:     React.ReactNode;
  /** Sidebar width (default: 252px) */
  sidebarWidth?: number;
  /** Detail panel width (default: 318px) */
  detailWidth?:  number;
}
```

The column widths must come from CSS custom properties so they can be
overridden at the page level without touching the component:

```css
.app-shell {
  --sidebar-width: 252px;
  --detail-width:  318px;
  display: grid;
  grid-template-columns: var(--sidebar-width) 1fr var(--detail-width);
  height: 100vh;
  overflow: hidden;
}
```

### 6.2  Panel sub-components

```tsx
// Correct usage
<Panel>
  <Panel.Header>
    <Panel.HeaderLeft>
      <NodeIcon kind={node.kind} size={34} />
      <div>
        <Text variant="heading">{node.name}</Text>
        <Text variant="body-sm" color="muted">{node.sub}</Text>
      </div>
    </Panel.HeaderLeft>
    <Panel.HeaderRight>
      <StatusBadge status={node.healthStatus} />
    </Panel.HeaderRight>
  </Panel.Header>

  <Panel.BackLink onClick={onBack}>Back to flow</Panel.BackLink>

  <Panel.Body>  {/* ← this element gets the scrollable class */}
    <Section label="Ownership">
      <KeyValueRow label="Team"  value={node.owningTeam} />
      <KeyValueRow label="Epic"  value={node.epicRef} href={epicUrl} />
    </Section>
  </Panel.Body>
</Panel>
```

---

## SECTION 7 — Accessibility

### 7.1  Required ARIA patterns

Every interactive widget must implement the correct ARIA role:

| Widget              | Pattern                                      |
|---------------------|----------------------------------------------|
| Tab bar (Flow/Seq/Graph) | `role="tablist"`, `role="tab"`, `role="tabpanel"` |
| Domain/Cap tree     | `role="tree"`, `role="treeitem"`, `aria-expanded` |
| Dropdown/Select     | `role="listbox"`, `role="option"`, `aria-selected` |
| Status badge        | `aria-label` describing both intent and text |
| Modal               | `role="dialog"`, `aria-modal`, focus trap    |
| Sidebar filter list | `role="list"`, `role="listitem"`             |
| Graph canvas        | `role="img"`, `aria-label` describing graph  |

### 7.2  Keyboard navigation

- All interactive elements reachable by `Tab`.
- Tree navigation: `Arrow Up/Down` moves between siblings, `Arrow Right`
  expands, `Arrow Left` collapses, `Home/End` jumps to first/last.
- Tab switcher: `Arrow Left/Right` cycles tabs (roving tabindex pattern).
- Select dropdown: `Arrow Up/Down` moves options, `Enter` selects,
  `Escape` closes, first character type-ahead jumps to first match.
- Modal: `Escape` closes, focus returns to trigger element.
- Graph canvas: selected node `Enter` opens detail panel, `Escape` clears.

### 7.3  Colour contrast

Every text/background combination must pass WCAG 2.1 AA (4.5:1 for normal
text, 3:1 for large text). Audit and fix the following known issues in the
existing design:
- `tokens.colors.text.muted` (`#888780`) on `tokens.colors.surface[0]`
  (`#FFFFFF`) = 3.8:1 — only passes for large text. Replace with `#6B6A66`
  for body text contexts or use a larger text size.
- `#3C3489` on `#EEEDFE` in accent badges = passes, keep.
- `#633806` on `#FAEEDA` in warning badges = passes, keep.

### 7.4  Focus indicators

All focusable elements must have a visible focus ring. Implement as a global
CSS rule using `:focus-visible` (not `:focus`) so mouse users don't see
the ring:

```css
:focus-visible {
  outline: 2px solid #7F77DD;
  outline-offset: 2px;
  border-radius: 4px;
}
```

---

## SECTION 8 — Extensibility Patterns

### 8.1  Enum-driven rendering

The `nodeKind`, `lifecycleStage`, `healthStatus`, `connectionType`, and
`interactionPattern` values come from the backend as string enums. Build
a single `registryEnumConfig` record so that adding a new enum value requires
changing only one file, not hunting through every `if`/`switch` in the codebase:

```typescript
// src/config/enumConfig.ts
export const nodeKindConfig: Record<NodeKind, {
  label:    string;
  icon:     string;           // Tabler icon name
  colorKey: keyof typeof tokens.colors.nodeKind;
}> = {
  SERVICE:         { label: 'Service',         icon: 'ti-server',        colorKey: 'SERVICE' },
  API_GATEWAY:     { label: 'API Gateway',     icon: 'ti-shield-check',  colorKey: 'API_GATEWAY' },
  MESSAGE_BROKER:  { label: 'Message Broker',  icon: 'ti-topology-ring', colorKey: 'MESSAGE_BROKER' },
  DATABASE:        { label: 'Database',        icon: 'ti-table',         colorKey: 'DATABASE' },
  EXTERNAL_SYSTEM: { label: 'External System', icon: 'ti-plug',          colorKey: 'EXTERNAL_SYSTEM' },
};

// Adding a new node kind in future = add one entry here.
// NodeIcon, StatusBadge, filter lists, all derive from this automatically.
```

Apply the same pattern to:
- `lifecycleStageConfig` (EXPERIMENTAL, ACTIVE, MAINTENANCE, DEPRECATED, RETIRED)
- `healthStatusConfig` (HEALTHY, DEGRADED, UNHEALTHY, UNKNOWN)
- `connectionTypeConfig` (REST, GRPC, GRAPHQL, SOAP, JDBC, ASB, KAFKA, …)
- `flowVariantConfig` (HAPPY_PATH, DEGRADED_MODE, COMPENSATING, CANARY)

### 8.2  Theme extension

The entire app must support a `theme` prop at the root that can override
any subset of the token system, enabling white-labelling or dark mode
without component changes:

```typescript
interface RegistryTheme {
  colors?:     DeepPartial<typeof tokens.colors>;
  typography?: DeepPartial<typeof tokens.typography>;
  radii?:      DeepPartial<typeof tokens.radii>;
  spacing?:    DeepPartial<typeof tokens.spacing>;
}

// Usage
<RegistryProvider theme={{ colors: { surface: { 0: '#1A1A18', 1: '#2A2A28' } } }}>
  <App />
</RegistryProvider>
```

The `RegistryProvider` writes resolved token values as CSS custom properties
to a wrapper element. Every component reads via `var(--...)` — no JS token
resolution at render time except in the provider itself.

### 8.3  Column and section extensibility

Every list-like component that renders rows (ConnectionRow, StepCard,
KeyValueRow) must accept an `actions` slot for appending custom controls
without forking the component:

```tsx
<ConnectionRow
  node={node}
  actions={<CopyButton value={node.id} />}
/>
```

Every panel section must accept children directly so new sections can be
inserted from a parent without modifying the component:

```tsx
<Panel.Body>
  <Section label="Custom section from plugin">
    <CustomContent />
  </Section>
</Panel.Body>
```

---

## SECTION 9 — Performance

### 9.1  Component memoisation rules

- Wrap every component in `React.memo` by default.
- Pass stable references for objects and callbacks (use `useMemo` and
  `useCallback` at the boundary where props are created, not inside every
  component).
- Never create object literals or array literals in JSX props unless they
  are derived from `useMemo`.

```tsx
// ❌ New object on every render — breaks memo
<NodeIcon style={{ marginTop: 4 }} />

// ✅ Token-based class, no inline object
<NodeIcon className={styles.nodeIcon} />
```

### 9.2  Virtualise long lists

The sidebar tree and flow lists can grow large. Any list that may exceed
50 items must use a virtual scroll implementation (e.g. `@tanstack/virtual`):

```typescript
// The Sidebar's cap-body lists (flow items per capability)
// must use useVirtualizer when itemCount > 50.
```

### 9.3  Code splitting

Every tab pane (Flow, Sequence, Graph) and every panel view (flow detail,
edge detail, capability detail) must be wrapped in `React.lazy` + `Suspense`
with a skeleton fallback:

```tsx
const GraphTab    = React.lazy(() => import('./tabs/GraphTab'));
const SequenceTab = React.lazy(() => import('./tabs/SequenceTab'));
const FlowTab     = React.lazy(() => import('./tabs/FlowTab'));
