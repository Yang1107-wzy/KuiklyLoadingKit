# State machine

The request state machine intentionally uses only:

```text
HIDDEN
VISIBLE(generation, request)
DESTROYED
```

Animation phases are presentation-only and do not belong to the request
lifecycle.

## Transitions

| Current | Event | Next | Effects |
|---|---|---|---|
| Hidden | Show(R) | Visible(g+1, R) | Render, optional schedule |
| Visible(A) | Show(B) | Visible(g+1, B) | Cancel A, REPLACED A, render B, optional schedule B |
| Visible | Hide | Hidden | Cancel, render hidden, MANUAL |
| Hidden | Hide | Hidden | None |
| Visible(g) | Timeout(g) | Hidden | Cancel, render hidden, TIMEOUT |
| Visible(g) | Timeout(old) | Visible(g) | None |
| Visible | UpdateMessage | Visible(updated) | Render |
| Hidden | UpdateMessage | Hidden | None |
| Visible | Destroy | Destroyed | Cancel, render destroyed, DESTROYED |
| Hidden | Destroy | Destroyed | Render destroyed |
| Destroyed | Any event | Destroyed | None |

Controller reuse after a View is destroyed creates a fresh state machine while
preserving the monotonically increasing generation.

## Dismissal invariant

Each generation can leave Visible only once. Once hidden/replaced/destroyed,
later hide or timeout events see no matching Visible generation and cannot
emit a second terminal reason.

## Replacement example

```text
t=0s  show(A, 5s)   -> generation 1
t=2s  show(B, 10s)  -> generation 1 REPLACED; generation 2 starts
t=5s  A callback     -> ignored because generation 1 is stale
t=12s B callback     -> generation 2 TIMEOUT; Hidden
```

## Test coverage

`LoadingStateMachineTest` covers initial state, show/hide, idempotent hide,
replacement, stale/current timeout, manual hide followed by timeout, null/zero/
negative timeout, update, destroy, dismissal uniqueness, and rapid switching.

`LoadingControllerTest` covers unbound calls, binding, ManualScheduler timeout
behavior, destroy/unbind, rebind isolation, reuse, animation independence, and
explicit default requests.
