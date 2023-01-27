package hexlet.code.app.controller;

import hexlet.code.app.dto.LabelDto;
import hexlet.code.app.model.Label;
import hexlet.code.app.repository.LabelRepository;
import hexlet.code.app.service.LabelService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.List;
import static hexlet.code.app.controller.LabelController.LABEL_CONTROLLER_PATH;
import static org.springframework.http.HttpStatus.CREATED;


@AllArgsConstructor
@RestController
@RequestMapping("${base-url}" + LABEL_CONTROLLER_PATH)
public class LabelController {

    public static final String LABEL_CONTROLLER_PATH = "/labels";
    public static final String ID = "/{id}";

    private final LabelRepository labelRepository;
    private final LabelService labelService;

    @Operation(summary = "Create new Label")
    @ApiResponse(responseCode = "201", description = "Label created")
    @PostMapping
    @ResponseStatus(CREATED)
    public Label createNewLabel(@RequestBody @Valid LabelDto labelDto) {
        return labelService.createLabel(labelDto);
    }

    @Operation(summary = "Get all labels")
    @ApiResponses(@ApiResponse(responseCode = "200", content =
    @Content(schema = @Schema(implementation = Label.class))
    ))
    @GetMapping
    public List<Label> getAllTask() {
        return labelRepository.findAll().stream().toList();
    }

    @Operation(summary = "Get label")
    @ApiResponses(@ApiResponse(responseCode = "200"))
    @GetMapping(ID)
    public Label getLabelById(@PathVariable Long id) {
        return labelRepository.findById(id).get();
    }

    @Operation(summary = "Update label")
    @ApiResponses(@ApiResponse(responseCode = "200"))
    @PutMapping(ID)
    public Label updateLabel(@RequestBody @Valid LabelDto labelDto,
                             @PathVariable Long id) {
        return labelService.updateLabel(labelDto, id);
    }

    @Operation(summary = "Delete label")
    @DeleteMapping(ID)
    public void deleteLabel(@PathVariable Long id) {
        labelRepository.deleteById(id);
    }
}
