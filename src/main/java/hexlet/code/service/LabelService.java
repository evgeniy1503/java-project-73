package hexlet.code.service;

import hexlet.code.dto.LabelDto;
import hexlet.code.model.Label;

public interface LabelService {

    Label createLabel(LabelDto labelDto);

    Label updateLabel(LabelDto labelDto, Long id);
}
