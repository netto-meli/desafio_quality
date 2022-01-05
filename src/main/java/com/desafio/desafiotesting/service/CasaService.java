package com.desafio.desafiotesting.service;

import com.desafio.desafiotesting.domain.Casa;
import com.desafio.desafiotesting.domain.Comodo;
import com.desafio.desafiotesting.domain.Bairro;
import com.desafio.desafiotesting.exception.RepositoryException;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class CasaService {

    List<Casa> casas = new ArrayList<>();
    List<Bairro> bairros = new ArrayList<>();

    private Bairro getBairro(String nome){
        return bairros.stream().filter(b -> b.getNome().equals(nome)).findFirst().orElse(null);
    }

    // Calcula area do comodo
    private double calcularAreaIndividual(Comodo comodo){
        return comodo.getLargura() * comodo.getComprimento();
    }

    //Calcula area total do Imovel
    private double calcularAreaTotal(Casa casa) {
        double areaTotal = 0;
        for (Comodo com : casa.getComodos()) areaTotal += calcularAreaIndividual(com);
        return areaTotal;
    }

    public List<Casa> findAll() {
        return casas.stream().map(x -> new Casa(x.getNome(), x.getBairro(), x.getComodos())).collect(Collectors.toList());
    }

    public Casa findByNome(String nome) {
        Optional<Casa> casa = casas.stream().filter(x -> x.getNome().equals(nome)).findFirst();
        return casa.orElse(null);
    }

    public String getAreaCasa(String nome) {
        Casa casa = findByNome(nome);
        if (casa == null) throw new RepositoryException("Casa inexistente.");
        double area = calcularAreaTotal(casa);
        return "A área total da casa é de: " + area + " metros.";
    }

    public String getValorCasa(String nome) {
        Casa casa = findByNome(nome);
        if (casa == null) throw new RepositoryException("Casa inexistente.");
        double area = calcularAreaTotal(casa);
        Bairro bairro = getBairro(casa.getBairro());
        if (bairro == null) throw new RepositoryException("Bairro inexistente.");
        BigDecimal valorM2 = bairro.getValorMetroQuadrado();
        BigDecimal valorCasa = valorM2.multiply(new BigDecimal(area));
        return "O valor da casa é: R$" + NumberFormat.getCurrencyInstance().format(valorCasa);
    }

    public String getMaiorComodo(String nome) {
        Casa casa = findByNome(nome);
        if (casa == null) throw new RepositoryException("Imovel inexistente.");
        double maiorComodo = 0;
        double areaComodo;
        String nomeComodo = "";
        for (Comodo c : casa.getComodos()) {
            areaComodo = calcularAreaIndividual(c);
            if(areaComodo > maiorComodo) {
                maiorComodo = areaComodo;
                nomeComodo = c.getNome();
            }
        }
        return "O maior comodo da casa " + nome + ": " +
                nomeComodo + " com área de " + maiorComodo + " metros quadrados.";
    }

    public String getAreaComodos(String nomeDaCasa) {
        StringBuilder areaComodos = new StringBuilder();
        Casa casa = casas.stream().filter( im -> im.getNome().equals(nomeDaCasa)).findFirst().orElse(null);
        if (casa == null) throw new RepositoryException("Casa inexistente.");
        for (Comodo com : casa.getComodos()) {
            areaComodos.append("Comodo: ").append(com.getNome()).append(" com ")
                    .append(calcularAreaIndividual(com)).append(" metros quadrados<br>");
        }
        return "A area de cada comodo da casa " + nomeDaCasa + " é: <br>" + areaComodos;
    }

    public void salvarCasa(Casa casa) {
        casas.add(casa);
    }

    public void salvarBairro(Bairro bairro) {
        bairros.add(bairro);
    }
}
