package admin;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Scanner;

import conexao_SQL.Conexao_SQL;
import pessoa.Carteira;
import pessoa.Multas;

public class Admin {
	
	private Carteira carteira; 
    private Multas multas; 

    public Admin(Carteira carteira, Multas multas) {
        this.carteira = carteira;
        this.multas = multas;
    }

    public void consultarTabelasPorCPF(String cpf) {
        try {
            Connection con = Conexao_SQL.run_connection();
          
            String queryVeiculos = "SELECT * FROM veiculo WHERE CNH = (SELECT CNH FROM pessoa_fisica WHERE CPF = ?)";
            PreparedStatement stmtVeiculos = con.prepareStatement(queryVeiculos);
            stmtVeiculos.setString(1, cpf);
            ResultSet rsVeiculos = stmtVeiculos.executeQuery();

            System.out.println("Veículos associados ao CPF " + cpf + ":");
            while (rsVeiculos.next()) {
                System.out.println("\n//////////////////////////////////\n");
                System.out.println(String.format("Placa: %s \n", rsVeiculos.getString("PLACA")));
                System.out.println(String.format("Nome do Titular: %s \n", rsVeiculos.getString("nome_titular")));
                System.out.println(String.format("Situação do Veículo: %s \n", rsVeiculos.getString("situacao_veiculo")));
                System.out.println(String.format("Valor da Multa: %s \n", rsVeiculos.getString("valor_multa")));
            }

            String queryCarteira = "SELECT * FROM situacao_carteira WHERE CNH = (SELECT CNH FROM pessoa_fisica WHERE CPF = ?)";
            PreparedStatement stmtCarteira = con.prepareStatement(queryCarteira);
            stmtCarteira.setString(1, cpf);
            ResultSet rsCarteira = stmtCarteira.executeQuery();

            System.out.println("\nSituação da Carteira associada ao CPF " + cpf + ":");
            while (rsCarteira.next()) {
                System.out.println("\n//////////////////////////////////\n");
                System.out.println(String.format("CNH: %s \n", rsCarteira.getString("CNH")));
                System.out.println(String.format("Nome: %s \n", rsCarteira.getString("nome")));
                System.out.println(String.format("Número de veículos cadastrados: %s \n", rsCarteira.getString("qty_veiculos")));
                System.out.println(String.format("Valor total da multa: %s \n", rsCarteira.getString("valor_multa")));
            }

        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("Erro ao consultar tabelas por CPF.");
        }
    }
    
    public void inserirMulta() {
        try {
            Scanner scanner = new Scanner(System.in);

            System.out.println("Digite o CPF do infrator:");
            String cpf = scanner.nextLine();

            System.out.println("Digite a placa do veículo:");
            String placa = scanner.nextLine();

            System.out.println("Digite o valor da multa:");
            int valorMulta = scanner.nextInt();
            scanner.nextLine(); 
            System.out.println("Digite o motivo da multa:");
            String motivoMulta = scanner.nextLine();

            Connection con = Conexao_SQL.run_connection();
           
            String updateQueryCarteira = "UPDATE situacao_carteira SET valor_multa = valor_multa + ? WHERE CNH = (SELECT CNH FROM pessoa_fisica WHERE CPF = ?)";
            PreparedStatement updateStmtCarteira = con.prepareStatement(updateQueryCarteira);
            updateStmtCarteira.setInt(1, valorMulta);
            updateStmtCarteira.setString(2, cpf);
            updateStmtCarteira.executeUpdate();

            String updateQueryVeiculo = "UPDATE veiculo SET situacao_veiculo = 'MULTADO', valor_multa = valor_multa + ? WHERE PLACA = ?";
            PreparedStatement updateStmtVeiculo = con.prepareStatement(updateQueryVeiculo);
            updateStmtVeiculo.setDouble(1, valorMulta);
            updateStmtVeiculo.setString(2, placa);
            updateStmtVeiculo.executeUpdate();

            String insertQueryMulta = "INSERT INTO multas (cpf, placa_veiculo, valor, descricao) VALUES (?, ?, ?, ?)";
            PreparedStatement insertStmtMulta = con.prepareStatement(insertQueryMulta);
            insertStmtMulta.setString(1, cpf);
            insertStmtMulta.setString(2, placa);
            insertStmtMulta.setDouble(3, valorMulta);
            insertStmtMulta.setString(4, motivoMulta);
            insertStmtMulta.executeUpdate();

            System.out.println("Multa aplicada com sucesso!");

        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("Erro ao aplicar a multa.");
        }
    }
    
    public void removerMulta(String cpf) {
        try {
            Connection con = Conexao_SQL.run_connection();
            Scanner scanner = new Scanner(System.in);
            
            System.out.println("Digite a CNH do infrator:");            
            String cnh = scanner.nextLine();

            System.out.println("Informe a placa do veículo: ");
            String placa = scanner.nextLine().toUpperCase();

            double valorMultaRemovida = multas.obterValorMulta(cpf, placa);
            double valorAtualMulta = carteira.getValorMulta(cnh);

            if (valorMultaRemovida <= valorAtualMulta) {
                String queryCarteira = "UPDATE situacao_carteira SET valor_multa = valor_multa - ? WHERE CNH = (SELECT CNH FROM pessoa_fisica WHERE CPF = ?)";
                PreparedStatement stmtCarteira = con.prepareStatement(queryCarteira);
                stmtCarteira.setDouble(1, valorMultaRemovida);
                stmtCarteira.setString(2, cpf);
                stmtCarteira.executeUpdate();

                String queryVeiculo = "UPDATE veiculo SET valor_multa = 0, situacao_veiculo = 'REGULAR' WHERE PLACA = ? AND CNH = (SELECT CNH FROM pessoa_fisica WHERE CPF = ?)";
                PreparedStatement stmtVeiculo = con.prepareStatement(queryVeiculo);
                stmtVeiculo.setString(1, placa);
                stmtVeiculo.setString(2, cpf);
                stmtVeiculo.executeUpdate();

                String queryRemoverMulta = "DELETE FROM multas WHERE cpf = ? AND placa_veiculo = ?";
                PreparedStatement stmtRemoverMulta = con.prepareStatement(queryRemoverMulta);
                stmtRemoverMulta.setString(1, cpf);
                stmtRemoverMulta.setString(2, placa);
                stmtRemoverMulta.executeUpdate();

                System.out.println("Multa removida com sucesso!");
            } else {
                System.out.println("Erro: O valor da multa a ser removida é maior do que o valor atual na carteira.");
            }

        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("Erro ao remover a multa.");
        }
    }


}
