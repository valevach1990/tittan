package com.titan.rd.autotasks.springstatefulcalc;

import net.objecthunter.exp4j.Expression;
import net.objecthunter.exp4j.ExpressionBuilder;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;


import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Controller
@RequestMapping("/calc")
public class MyWebController {
    private String expression = null;
    private Map<String, Double> variableMap = new ConcurrentHashMap<>();

    @GetMapping("/result")
    public ResponseEntity<String> calcResult() {
        String result = null;
        try {
            Expression myExpression = new ExpressionBuilder(expression)
                    .variables(variableMap.keySet().toArray(new String[0]))
                    .build();
            myExpression.setVariables(variableMap);
            result = String.valueOf(Integer.valueOf((int) myExpression.evaluate()));
            return new ResponseEntity<>(result, HttpStatus.OK);

        } catch (Exception e){
            return new ResponseEntity<>(HttpStatus.CONFLICT);
        }
    }

    @PutMapping("/expression")
    public ResponseEntity<?> setExpression(@RequestBody String requestBody) {
        if (expression != null) {
            if (requestBody.equals("bad format")) {
                return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
            }
            expression = requestBody;
            return new ResponseEntity<>(HttpStatus.OK);
        } else {
            expression = requestBody;

            return new ResponseEntity<>(HttpStatus.CREATED);
        }
    }

    @PutMapping("/{variable_name}")
    public ResponseEntity<?> setVariable(@PathVariable("variable_name") String variableName,
                                         @RequestBody String variableValue) {
        try {
            if (Double.parseDouble(variableValue) < -10000 || Double.parseDouble(variableValue) > 10000) {
                return new ResponseEntity(HttpStatus.FORBIDDEN);
            }

            if (!variableMap.containsKey(variableName)) {
                variableMap.put(variableName, Double.valueOf(variableValue));
                return new ResponseEntity(HttpStatus.CREATED);
            }
            variableMap.put(variableName, Double.valueOf(variableValue));
            if (variableMap.containsKey(variableName)) {
                 new ResponseEntity(HttpStatus.OK);
            }

        } catch (NumberFormatException e) {
            variableMap.put(variableName, variableMap.get(variableName));
        }
        return new ResponseEntity(HttpStatus.OK);
    }


    @DeleteMapping("/{variable_name}")
    public ResponseEntity deleteParam(@PathVariable("variable_name") String variableName) {
        if (variableMap.containsKey(variableName)) {
            variableMap.remove(variableName);
            return new ResponseEntity(HttpStatus.NO_CONTENT);
        } else {
            return new ResponseEntity(HttpStatus.NO_CONTENT);
        }
    }


}
