package com.sap.emission.data;

import java.io.Serializable;

//import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

//@Schema(description = "Custom error response")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CustomErrorResponse implements Serializable {

    private static final long serialVersionUID = -7755563009111273632L;

   // @Schema(description = "Text that provide more details and corrective actions related to the error")
    public String errorMessage;

}
