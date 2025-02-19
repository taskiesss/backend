package taskaya.backend.DTO.freelancers.requests;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import taskaya.backend.entity.freelancer.EmployeeHistory;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class EmployeeHistoryPatchDTO {
    List<EmployeeHistory> employeeHistory;
}

