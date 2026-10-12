# Pre-publication audit

Audited October 12, 2026. Reference: the project brief, supplied exam clarifications, and documented interpretations. **The original Polish exam document is absent; full exam compliance is not established.** No commits, pushes, or Git initialization were performed.

This report retains the first audit as history; the [repeat audit](#repeat-audit-of-the-unchanged-implementation) records the latest pass and its documentation-only correction.

## Initial read-only findings

All source, tests, documentation, Maven configuration, UML, workflow, ignore rules, and license were reviewed before edits. The baseline `mvn verify` passed all **35 tests**. Line numbers in this table refer to the pre-audit source.

| Finding | File and baseline lines | Evidence and correction |
| --- | --- | --- |
| Confirmed termination defect | `src/main/java/puzzle/Referee.java:38–53`; `Player.java:13–23` | After Greedy solved a 2×2 board with score 1, the returned referee accepted FlipRow and changed the score to failure 5. Budget checking did not enforce termination. Added referee termination checking, preserving Systematic's full schedule. |
| Meaningful avoidable scanning | `src/main/java/puzzle/Board.java:48–50`; `Player.java:16` | Solved checks scanned up to n² cells per action, giving an O(n⁴) game upper bound. Board now maintains a zero count through construction and `set`. |
| Test gaps | `src/test/java/puzzle/CommandTest.java:77–80`; `TournamentTest.java:19–63` | Single-cell tests covered only rotations/sorts; invalid-coordinate tests did not cover all indexed operations; no eight-player bracket assertions existed. Added deterministic boundary and bracket cases. |
| Incorrect documentation/hygiene claim | `docs/report.md:81–99` | The report contained temporary absolute paths and claimed the project contained none. Replaced the machine-specific historical report with this audit. |
| Minor input-validation defect | `src/main/java/puzzle/Main.java:6–13` | The documented two-argument CLI ignored surplus arguments. Combined mode and argument-count validation; added a rejection test. |

No incorrect command transformation, inverse algorithm, strategy phase ordering, or tournament advancement was found. The command families, abstract lifecycle guard, and strategy interface have actual multiple implementations; no class or dependency warranted removal. Dense one-line methods are a readability preference, not a confirmed defect, and were not broadly reformatted.

## File-by-file review

| File(s) | Result |
| --- | --- |
| `Board.java` | Positive/square validation, defensive copies, boolean representation, bounds, and formatting are sound. Added zero-count invariant; copy/random initialization and no-op writes tested. |
| `Referee.java` | Retains command references, execution/undo action numbers, and an ArrayDeque of active entries. Added termination enforcement; failed submissions do not count. |
| `Player.java` | Prepares using size only before exposing cells, creates an independent referee, and rejects turns performing other than one action. Uses referee termination state. |
| `Tournament.java` | Registration order survives advancement; one board per round; correct ties/byes. No implementation changes needed. Small result records are appropriate data holders. |
| `Main.java` | One console entry point; two modes and seed. Surplus arguments now rejected. Invalid numeric seeds already fail explicitly. |
| `StrategyComparison.java` | Shared board collection, independent games, real timing, and correct aggregates. No benchmark or optimality claim. No change needed. |
| `commands/Command.java` | Useful interface for eight basic operations and composites. Size-only validation supports pre-reveal construction. |
| `commands/AbstractCommand.java` | Common single-execution/single-undo/same-board lifecycle guard. Appropriate abstraction with nine concrete implementations. |
| `commands/SetCellCommand.java` | Saves one previous boolean; validates both indices before mutation. Correct for either value and size 1. |
| `commands/FlipRowCommand.java` | Correct row inversion and self-inverse undo; no saved line. |
| `commands/FlipColumnCommand.java` | Correct column inversion and self-inverse undo; no saved line. |
| `commands/SetDiagonalCommand.java` | Main index (r,r); secondary index (r,n−1−r). Saves only diagonal values. Both choices/values tested. |
| `commands/SortRowCommand.java` | Counts zeros and writes zeros before ones in linear time. Saves affected row for exact undo. |
| `commands/SortColumnCommand.java` | Same correct binary sorting logic on a column; stores only that column. |
| `commands/RotateRowCommand.java` | Right shift using the previous last cell; inverse left shift. Full-board expected output verifies unaffected cells. |
| `commands/RotateColumnCommand.java` | Down shift using the previous bottom cell; inverse up shift. Full-board expected output verifies unaffected cells. |
| `commands/CompositeCommand.java` | Rejects nesting, duplicate families, invalid coordinates, wrong size, and previously used components. Preflights before mutation, executes all children, reverses order for undo. |
| `strategies/PlayerStrategy.java` | Preparation/action interface provides real interchangeability. Full-schedule flag preserves the specified Systematic exception. |
| `strategies/RandomStrategy.java` | Valid seeded selection; bounded queue fixes composites before reveal. O(n²) preparation/storage is justified by that restriction; no speculative redesign. |
| `strategies/CautiousStrategy.java` | Reuses Random selection. 20% draw schedules the next undo; no consecutive self-scheduled undos. Resets pending state. |
| `strategies/SystematicStrategy.java` | Row-major sets of every cell; exactly n² actions, including initially solved boards. Resets traversal. |
| `strategies/GreedyStrategy.java` | Row-major search sets zeros only; score equals initial zero count. Resets traversal. |
| `strategies/AdvancedStrategy.java` | Row sorting, strict-majority column flips, Greedy finish. Even-column ties remain unflipped. Repeated games reset all phases. |
| `BoardTest.java` | Added zero-count consistency tests. |
| `CommandTest.java` | Added all single-cell operations and all indexed families' invalid-coordinate checks; retained existing manual examples/undo tests. |
| `RefereeTest.java` | Added post-termination rejection, initial solution, last-unit undo, and repeated empty undo tests. |
| `StrategyTest.java` | Added Advanced resets, Random/Cautious replay across changing sizes, and CLI surplus-argument rejection. |
| `TournamentTest.java` | Added brackets for 1, 2, 3, 5, 8 entrants; checks pairing and shared boards throughout every round. |
| `pom.xml` | Java release 17; only direct dependency is test-scoped JUnit 5; compiler/Surefire/JAR plugins versioned; main manifest correct. No profiles or unused libraries. |
| `.github/workflows/ci.yml` | Checkout, Temurin 17, Maven cache, read-only permissions, `mvn --batch-mode verify`; no secrets or deployment. Hosted execution not verified. |
| `.gitignore`, `LICENSE` | Standard build/IDE exclusions and complete MIT text. Git tracking unavailable; ignore rules do not prove tracking state. |
| `README.md` | Clear game description, natural OOP, sparse undo, run examples, test instructions, and measured comparison. Updated timing values and audit link. |
| `docs/design.md` | Rules and ownership contracts explicit; UML names/relationships match code by source inspection. Updated termination and complexity. Mermaid rendering was not executed. |
| `docs/report.md` | Replaced the obsolete machine-specific implementation report with this audit. |

## Requirements verification

VERIFIED below means tested locally against the provisional project specification, not certified against the unavailable original exam.

| Requirement | Status | Evidence |
| --- | --- | --- |
| Complete original exam compliance | PARTIAL | Original document absent; supplied clarifications and documented rules reviewed. |
| Positive/square board, bounds, copy isolation, goal state | VERIFIED | BoardTest; full-board command assertions; zero-count consistency test. |
| Eight basic operations, valid values, intended-cell isolation | VERIFIED | CommandTest's 12 manually calculated cases and full-board assertions. |
| Every basic operation at n=1 and exact undo | VERIFIED | Both initial values across all eight families, both diagonals and both set values. |
| Invalid coordinates before mutation | VERIFIED | All seven coordinate-bearing families, negative/out-of-range indices, both SetCell indices. |
| Execute once, undo once, same board | VERIFIED | Lifecycle tests reject repeated execution/undo and a different board. |
| LIFO undo, preserved history, empty/repeated undo | VERIFIED | RefereeTest checks board, history, action numbers, and repeated empty attempts. |
| Sparse undo memory | VERIFIED | Source review: one boolean/line/diagonal or inverse operations; no per-command board snapshot. |
| Composite family restrictions, order, final state, undo | VERIFIED | Validation and overlapping-command tests; n=1 temporary solution executes the later child. |
| Composite preparation before reveal | VERIFIED | Player.prepare receives size only; Random.prepare fixes all composites; source inspection. External callers must honor the same contract. |
| Top-level counts and composite cost | VERIFIED | RefereeTest checks execution/undo counts and composite cost of one. |
| Undo consumes budget; 20% cautious probability | INTENTIONAL DEVIATION | Explicit project interpretations, not proven deviations from an unavailable original. Constant/probability logic inspected; seeded undo behavior tested. |
| Systematic's n² schedule | VERIFIED | All-zero, mixed, and already solved inputs; preserved after termination fix. |
| Greedy score equals initial zeros | VERIFIED | Mixed board score 5, all-zero 3×3 score 9, solved board score 0; reset tests. |
| Advanced ordering, majority, termination, resets | VERIFIED | Manual phase states, even tie, n=1 failure budget, repeated games. |
| Random/Cautious validity and reproducibility | VERIFIED | Seeded 10×10 games; repeated size sequence 2,3,1,4; termination and history assertions. |
| Termination and no actions afterward | VERIFIED | New regression rejects execution and undo without altering final board/history/score. |
| Success/failure scores and exact budget boundary | VERIFIED | RefereeTest and StrategyTest; last-unit undo cannot exceed limit. |
| Pairing, round fairness, tie-breaking, byes, winner | VERIFIED | TournamentTest for 1,2,3,5,8 players, all ties, lower-score winner, and seed replay. |
| Strategy comparison on identical boards | VERIFIED | Source review of common board collection/copies; comparison executed. |
| Clean-source build and documented console modes | VERIFIED | Clean source copy built with cached dependencies; Maven and both JAR modes executed. |
| Hosted GitHub Actions | IMPLEMENTED BUT NOT TESTED | Workflow reviewed; no remote repository run. |
| Git tracked-file hygiene and final Git diff | PARTIAL | Git reports no repository; source-only snapshot diff reviewed instead. |
| Java 21 compatibility | IMPLEMENTED BUT NOT TESTED | Java 17 release target; only JDK 17 tested locally. |

## Changes and complexity

Production changes are limited to `Board`, `Referee`, `Player`, and CLI validation in `Main`; no command or strategy algorithms were replaced. Added regression/edge assertions in the existing five test classes. Updated README/design and this report. No classes, significant functionality, packages, dependencies, or game rules were removed or added.

A solved check now takes O(1), using Board's zero count. A cell operation remains O(1); flips, binary sorts, rotations, and diagonal operations take O(n). Inverse undo has the same bounds; sort/diagonal commands save O(n) values, while flips/rotations use O(1) extra space. Composite work is O(kn), with at most eight children. Board construction/copy/display remain O(n²).

Complete Systematic, Greedy, and Advanced games now take O(n²) time and O(n²) board/history space. Random/Cautious games take O(n³) worst-case time and retained history space, plus O(n²) prepared composites. With p entrants there are p−1 matches, O(log p) rounds, an O(p n_max³) time upper bound, and O(n_max³+p n_max²) peak reachable space. The bound includes prepared-command queues retained by registered Random/Cautious players, including eliminated players. The [design table](design.md#complexity) gives the detailed bounds. This is algorithm analysis, not a measured speedup claim.

No objectively unnecessary class, dependency, generic framework, manager, or dead algorithm was found. Row/column code is short explicit axis-specific manipulation; replacing it with a generic matrix framework would make this project harder to explain. Command validation and inverse state are useful, not removable boilerplate.

## Verification results

Toolchain: Temurin OpenJDK 17.0.20.1 and Maven 3.9.9. Verification used temporary tool installations and a temporary Maven dependency cache; no machine paths are embedded in project files.

| Executed check | Result |
| --- | --- |
| Baseline `mvn verify` | 35 tests passed. |
| Final `mvn clean test` | 48 tests passed; 0 failures/errors/skips. |
| Final `mvn package` | Passed; tests rerun and executable JAR produced. |
| Clean source copy: `mvn --batch-mode verify` | Passed with cached Maven dependencies; not a Git checkout or empty dependency cache. |
| `javac --release 17 -Xlint:all` over main sources | Passed without compiler warnings. |
| Tournament, seed 42 | Winner Advanced; round sizes 88, 46, 87; final score Advanced 468 vs Greedy 3823. |
| Default console mode | Output matched `tournament 42`. |
| Comparison, seed 42 | Twenty shared 10×10 boards; scores/success rates below. |
| Relative Markdown links and UML | File links resolved; diagram types/relationships checked against source; no rendered Mermaid validation. |
| Source hygiene | No class/JAR/IDE files outside generated target; ignore rule present; no absolute machine paths or obvious private-key/AWS-key patterns found in source/docs/config. This is not a guarantee that every possible secret format is absent. |
| Source preservation/diff | Pre-edit source snapshot retained outside project; source-only changes reviewed. Git status/diff/tracked-file checks unavailable. |

Final test counts: Board 4; Command 19; Referee 6; Strategy 10; Tournament 9 (48 total).

| Strategy | Average score | Solved | Average ms/game in this run |
| --- | ---: | ---: | ---: |
| Random | 101.00 | 0% | 1.980 |
| Cautious | 101.00 | 0% | 0.883 |
| Systematic | 100.00 | 100% | 0.182 |
| Greedy | 48.60 | 100% | 0.114 |
| Advanced | 25.50 | 100% | 0.140 |

Times include preparation, copies, and game execution; exclude board generation/printing. There is no warm-up or statistical control; these values do not support reliable runtime rankings or universal optimality. The run/build commands are in [README](../README.md#build-and-run).

Remaining limits: original exam unavailable; hosted CI and JDK 21 untested; no usable Git metadata; fresh dependency downloads and byte-identical JAR reproducibility were not verified. Direct commands require reverse-order undo and exclusive composite-component ownership as documented. The normal referee enforces LIFO ordering; the API is not designed to defend against a caller independently mutating retained command objects.

## Assessment and publication decision

Ratings assess this student project's scope, not enterprise requirements. A 4 means sound with the stated practical limitations; a 5 is reserved for a particularly strong fit.

| Area | Rating / 5 | Reason |
| --- | ---: | --- |
| Java code quality | 4 | Encapsulated state, explicit validation, no compiler warnings; compact formatting sometimes slows reading. |
| Simplicity | 4 | Small direct model; prebuilt composites and lifecycle contracts require explanation but serve real requirements. |
| OOP design | 4 | Command/Strategy polymorphism and composition have concrete uses; inheritance is limited. |
| Algorithm correctness | 4 | Manual expected states and edge tests pass; full original exam compliance remains provisional. |
| Efficiency | 4 | Constant-time goal checks and sparse undo; bounded random preparation is an explicit memory tradeoff. |
| Undo implementation | 4 | Exact LIFO restoration/history tested; direct command callers must respect ownership. |
| Testing | 4 | 48 deterministic tests cover major behavior and boundaries; no claim of exhaustive verification. |
| Maintainability | 4 | Clear packages and responsibilities; few implicit lifecycle conventions remain. |
| Student-level appropriateness | 5 | Arrays, loops, collections, interfaces, and a small amount of inheritance; no extra framework. |
| README quality | 4 | Clear purpose/run instructions, real results, and explicit limitations; detailed audit stays outside README. |
| GitHub readiness | 4 | Source/build/docs ready; Git tracking and hosted CI must be checked when a repository exists. |

The project demonstrates meaningful Java, mutable-state reasoning, interfaces, arrays/collections, inverse operations, and deterministic testing. The OOP design is credible and appropriately challenging for a beginner-to-intermediate student. The README gives a technically accurate first impression without inflated claims. As a Java OOP example it can complement Python and C++ work; those repositories were not audited here, so relative quality and duplication are not assessed. It is reasonable to include this project in an SWE internship portfolio if you can explain the code and tradeoffs yourself; no hiring outcome is implied.

**READY TO PUBLISH.** The confirmed defect is fixed, the meaningful scanning inefficiency is removed, and local verification passes. This decision concerns the source project; it does not certify the absent original exam or unexecuted hosted checks. No commits or pushes were made.

Yes: against the documented project specification, this is a correct, reasonably efficient, simple Java student project suitable for publication and an internship interview discussion. Be prepared to explain zero-count consistency, command ownership, undo accounting, Systematic's full schedule, composite preparation before reveal, and why command count differs from elapsed time.

## Repeat audit of the unchanged implementation

The repeated audit request was checked against the previous final source snapshot: all 35 project files were unchanged before this pass, and the read-only baseline `mvn verify` again passed all 48 tests. The earlier corrections above remain historical changes; no Java source, test, dependency, workflow, or game rule was changed during this repeat pass.

One documentation defect was found: the former tournament space bound at `docs/design.md:119` and `docs/report.md:95` omitted prepared-command queues retained by registered players. `Tournament.java:11–19` retains all registered players, while `RandomStrategy.java:10–19` retains an O(n²) queue per Random/Cautious player. The corrected bound is O(n_max³ + p n_max²), including the current game history and those queues. Only these two documentation files were edited. All existing requirements statuses, ratings, ownership limitations, and the publication decision above continue to apply.

Repeat-pass verification also passed: `mvn clean test` (48 tests, zero failures/errors/skips), `mvn package`, both seed-42 console modes, production `javac -Xlint:all` without warnings, and `mvn --batch-mode verify` in a clean source copy using cached dependencies. Source hygiene patterns, relative file links, and UML type consistency were checked again. The source-only diff contains exactly the two documentation files named above; Git status remains unavailable because there is no usable repository metadata.

The tournament again ended Advanced 468 versus Greedy 3823. The latest comparison measured:

| Strategy | Average score | Solved | Average ms/game |
| --- | ---: | ---: | ---: |
| Random | 101.00 | 0% | 1.260 |
| Cautious | 101.00 | 0% | 0.469 |
| Systematic | 100.00 | 100% | 0.104 |
| Greedy | 48.60 | 100% | 0.102 |
| Advanced | 25.50 | 100% | 0.085 |

These elapsed times are another single descriptive run; the README retains its accurately labeled earlier measurement. No timing ranking or measured speedup is claimed.
