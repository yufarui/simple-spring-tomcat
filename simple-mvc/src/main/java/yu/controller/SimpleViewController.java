package yu.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * @date: 2020/6/28 9:50
 * @author: farui.yu
 */
@Controller
public class SimpleViewController {

    @GetMapping("/index")
    public String test() {
        return "index";
    }
}
