package dev.nicotopia.aoc2023;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import dev.nicotopia.MultiHashMap;
import dev.nicotopia.Range;
import dev.nicotopia.RangeSet;
import dev.nicotopia.aoc.AocException;
import dev.nicotopia.aoc.DayBase;

public class Day19 extends DayBase {
    private enum WorkflowStepType {
        LESS, GREATER, ALWAYS
    }

    private record PartRating(int x, int m, int a, int s) {
        public int get(char field) {
            return switch (field) {
                case 'x' -> this.x;
                case 'm' -> this.m;
                case 'a' -> this.a;
                case 's' -> this.s;
                default -> throw new AocException("Illegal field: %c", field);
            };
        }
    }

    private class PartRatingSet {
        private final RangeSet x;
        private final RangeSet m;
        private final RangeSet a;
        private final RangeSet s;

        public PartRatingSet() {
            this(new RangeSet(new Range(1, 4000)), new RangeSet(new Range(1, 4000)), new RangeSet(new Range(1, 4000)),
                    new RangeSet(new Range(1, 4000)));
        }

        private PartRatingSet(RangeSet x, RangeSet m, RangeSet a, RangeSet s) {
            this.x = x;
            this.m = m;
            this.a = a;
            this.s = s;
        }

        public boolean isEmpty() {
            return this.x.isEmpty() || this.m.isEmpty() || this.a.isEmpty() || this.s.isEmpty();
        }

        public Map<String, List<PartRatingSet>> process(List<WorkflowStep> steps) {
            MultiHashMap<String, PartRatingSet> result = new MultiHashMap<>();
            for (WorkflowStep step : steps) {
                if (this.isEmpty()) {
                    break;
                }
                PartRatingSet cutOut;
                if (step.type == WorkflowStepType.ALWAYS) {
                    cutOut = this;
                } else {
                    Range toCutOut = step.type == WorkflowStepType.LESS ? new Range(1, step.reference - 1)
                            : new Range(step.reference + 1, 4000 - step.reference);
                    cutOut = switch (step.field) {
                        case 'x' ->
                            new PartRatingSet(this.x.cutOut(toCutOut), this.m.clone(), this.a.clone(), this.s.clone());
                        case 'm' ->
                            new PartRatingSet(this.x.clone(), this.m.cutOut(toCutOut), this.a.clone(), this.s.clone());
                        case 'a' ->
                            new PartRatingSet(this.x.clone(), this.m.clone(), this.a.cutOut(toCutOut), this.s.clone());
                        case 's' ->
                            new PartRatingSet(this.x.clone(), this.m.clone(), this.a.clone(), this.s.cutOut(toCutOut));
                        default -> throw new AocException("Illegal field: %c", step.field);
                    };
                }
                if (!cutOut.isEmpty()) {
                    result.getOrEmptyList(step.target).add(cutOut);
                }
            }
            return result;
        }

        public long count() {
            return this.x.count() * this.m.count() * this.a.count() * this.s.count();
        }
    }

    private record WorkflowStep(char field, WorkflowStepType type, int reference, String target) {
        public WorkflowStep(String target) {
            this((char) 0, WorkflowStepType.ALWAYS, 0, target);
        }

        public boolean matches(PartRating partRating) {
            return switch (this.type) {
                case LESS -> partRating.get(this.field) < this.reference;
                case GREATER -> this.reference < partRating.get(this.field);
                case ALWAYS -> true;
            };
        }

        @Override
        public String toString() {
            return switch (this.type) {
                case ALWAYS -> this.target;
                case LESS -> String.format("%c<%d:%s", this.field, this.reference, this.target);
                case GREATER -> String.format("%c>%d:%s", this.field, this.reference, this.target);
            };
        }
    }

    private Map<String, List<WorkflowStep>> workflows;
    private List<PartRating> partRatings;

    private void processInput() {
        Pattern workflowStepPattern = Pattern.compile("(([xmas])([<>])(\\d+):)?([ARa-z]+)");
        this.workflows = this.getPrimaryPuzzleInput().stream().filter(l -> !l.isEmpty() && !l.startsWith("{"))
                .collect(Collectors.toMap(l -> l.substring(0, l.indexOf('{')),
                        l -> Arrays.stream(l.substring(l.indexOf('{') + 1, l.length() - 1).split(","))
                                .map(d -> workflowStepPattern.matcher(d)).filter(Matcher::matches)
                                .map(m -> m.group(2) == null ? new WorkflowStep(m.group(5))
                                        : new WorkflowStep(m.group(2).charAt(0), switch (m.group(3).charAt(0)) {
                                            case '<' -> WorkflowStepType.LESS;
                                            case '>' -> WorkflowStepType.GREATER;
                                            default -> throw new AocException("Illegal workflow step: %s", m.group(3));
                                        }, Integer.valueOf(m.group(4)), m.group(5)))
                                .toList()));
        Pattern ratingsPattern = Pattern.compile("\\{x=(\\d+),m=(\\d+),a=(\\d+),s=(\\d+)\\}");
        this.partRatings = this.getPrimaryPuzzleInput().stream().map(ratingsPattern::matcher).filter(Matcher::matches)
                .map(m -> new PartRating(Integer.valueOf(m.group(1)), Integer.valueOf(m.group(2)),
                        Integer.valueOf(m.group(3)), Integer.valueOf(m.group(4))))
                .toList();
    }

    private boolean process(PartRating partRating) {
        String nextWorkflow = "in";
        while (!nextWorkflow.equals("A") && !nextWorkflow.equals("R")) {
            nextWorkflow = this.workflows.get(nextWorkflow).stream().filter(s -> s.matches(partRating)).findFirst()
                    .get().target;
        }
        return nextWorkflow.equals("A");
    }

    private int partOne() {
        return this.partRatings.stream().filter(this::process).mapToInt(r -> r.x + r.m + r.a + r.s).sum();
    }

    private long partTwo() {
        List<PartRatingSet> accepted = new ArrayList<>();
        MultiHashMap<String, PartRatingSet> map = new MultiHashMap<>();
        map.put("in", Arrays.asList(new PartRatingSet()));
        while (!map.isEmpty()) {
            map = map.entrySet().stream()
                    .map(e -> e.getValue().stream().map(p -> p.process(this.workflows.get(e.getKey())))).flatMap(s -> s)
                    .collect(MultiHashMap::new, MultiHashMap::addAllToLists, MultiHashMap::addAllToLists);
            map.remove("R");
            accepted.addAll(map.removeOrEmptyList("A"));
        }
        return accepted.stream().mapToLong(PartRatingSet::count).sum();
    }

    @Override
    public void run() {
        this.addDefaultExamplePresets();
        this.addTask("Process input", this::processInput);
        this.addTask("Part one", this::partOne);
        this.addTask("Part two", this::partTwo);
    }
}