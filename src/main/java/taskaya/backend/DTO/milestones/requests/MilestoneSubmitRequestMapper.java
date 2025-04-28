package taskaya.backend.DTO.milestones.requests;

import taskaya.backend.entity.work.Milestone;

import java.util.Comparator;
import java.util.Date;
import java.util.List;

public class MilestoneSubmitRequestMapper {
    public static Milestone toMilestone(MilestoneSubmitRequestDTO milestone) {
        Milestone myMilestone = Milestone.builder()
                .name(milestone.getTitle())
                .description(milestone.getDescription())
                .dueDate(milestone.getDueDate())
                .number(milestone.getMilestoneNumber())
                .estimatedHours(milestone.getExpectedHours())
                .status(Milestone.MilestoneStatus.NOT_STARTED)
                .build();
        return myMilestone;
    }
    public static List<Milestone> toMilestoneList(List<MilestoneSubmitRequestDTO> milestones) {
        List<Milestone> sortedMilestones = milestones.stream()
                .map(MilestoneSubmitRequestMapper::toMilestone)
                .sorted(Comparator.comparing(Milestone::getNumber))
                .toList();

        for (int i = 0; i < sortedMilestones.size() - 1; i++) {
            Milestone current = sortedMilestones.get(i);
            Milestone next = sortedMilestones.get(i + 1);

            if (!current.getDueDate().before(next.getDueDate())) {
                throw new IllegalArgumentException(
                        "Milestone number " + current.getNumber() +
                                " must have a due date before milestone number " + next.getNumber()
                );
            }
        }

        return sortedMilestones;
    }
}
