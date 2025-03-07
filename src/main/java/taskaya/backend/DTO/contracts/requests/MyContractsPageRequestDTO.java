package taskaya.backend.DTO.contracts.requests;

import lombok.Builder;
import lombok.Data;

import taskaya.backend.entity.enums.SortDirection;
import taskaya.backend.entity.enums.SortedByForContracts;
import taskaya.backend.entity.work.Contract;

import java.util.List;

@Data
@Builder
public class MyContractsPageRequestDTO {
    private String search;
    private List<Contract.ContractStatus> contractStatus;
    private int page;
    private int size;
    private SortedByForContracts sortedBy;
    private SortDirection sortDirection;
}
