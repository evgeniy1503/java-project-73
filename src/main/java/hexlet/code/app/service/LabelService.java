package hexlet.code.app.service;

import hexlet.code.app.dto.LabelDto;
import hexlet.code.app.model.Label;

public interface LabelService {

    Label createLabel(LabelDto labelDto);

    Label updateLabel(LabelDto labelDto, Long id);
}
