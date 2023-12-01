package pessoa;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import conexao_SQL.Conexao_SQL;

public class Veiculo {
	
	private Pessoa pessoa; 

    public Veiculo(Pessoa pessoa) {
        this.pessoa = pessoa;
    }

    public String consultarVeiculosMultadosPorCNH(String CNH) {
        try {
            Connection con = Conexao_SQL.run_connection();
            String query = "SELECT * FROM veiculo WHERE cnh = ? AND situacao_veiculo = 'MULTADO'";
            PreparedStatement stmt = con.prepareStatement(query);
            stmt.setString(1, CNH);

            ResultSet rs = stmt.executeQuery();

            StringBuilder result = new StringBuilder();

            while (rs.next()) {
                result.append("\n//////////////////////////////////\n");
                result.append(String.format("Placa: %s \n", rs.getString("PLACA")));
                result.append(String.format("Nome do Titular: %s \n", rs.getString("nome_titular")));
                result.append(String.format("Situação do Veículo: %s \n", rs.getString("situacao_veiculo")));
                result.append(String.format("Valor da Multa: %s \n", rs.getString("valor_multa")));
            }

            return result.toString();
        } catch (SQLException e) {
            e.printStackTrace();
            return "Erro ao obter dados do veículo.";
        }
    }

    public double obterValorMultaPorVeiculo(String placa) {
        try {
            Connection con = Conexao_SQL.run_connection();
            String query = "SELECT valor_multa FROM veiculo WHERE PLACA = ?";
            PreparedStatement stmt = con.prepareStatement(query);
            stmt.setString(1, placa);

            ResultSet rs = stmt.executeQuery();

            double valorMulta = 0;
            if (rs.next()) {
                valorMulta = rs.getDouble("valor_multa");
            }

            return valorMulta;
        } catch (SQLException e) {
            e.printStackTrace();
            return 0;
        }
    }

    public void pagarMulta(String placa, double valorPago) {
        try {
            Connection con = Conexao_SQL.run_connection();
            double valorMultaAtual = obterValorMultaPorVeiculo(placa);

            if (valorPago >= valorMultaAtual) {
                String updateQuery = "UPDATE veiculo SET valor_multa = 0, situacao_veiculo = 'REGULAR' WHERE PLACA = ?";
                PreparedStatement updateStmt = con.prepareStatement(updateQuery);
                updateStmt.setString(1, placa);
                updateStmt.executeUpdate();

                System.out.println("Multa paga com sucesso para o veículo com placa " + placa + "!");
            } else {
                double novoValorMulta = valorMultaAtual - valorPago;
                String updateQuery = "UPDATE veiculo SET valor_multa = ? WHERE PLACA = ?";
                PreparedStatement updateStmt = con.prepareStatement(updateQuery);
                updateStmt.setDouble(1, novoValorMulta);
                updateStmt.setString(2, placa);
                updateStmt.executeUpdate();

                System.out.println("Parte da multa paga para o veículo com placa " + placa + "! Valor restante: " + novoValorMulta);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("Erro ao pagar a multa do veículo com placa " + placa);
        }
    }
    
    public String consultarVeiculos(String CNH) {
    	try {
            Connection con = Conexao_SQL.run_connection();
            String query = "SELECT * FROM veiculo WHERE cnh = ?";
            PreparedStatement stmt = con.prepareStatement(query);
            stmt.setString(1, CNH);

            ResultSet rs = stmt.executeQuery();

            StringBuilder result = new StringBuilder();

            while (rs.next()) {
                result.append("\n//////////////////////////////////\n");
                result.append(String.format("Placa: %s \n", rs.getString("PLACA")));
                result.append(String.format("Nome do Titular: %s \n", rs.getString("nome_titular")));
                result.append(String.format("Situação do Veículo: %s \n", rs.getString("situacao_veiculo")));
                result.append(String.format("Valor da Multa: %s \n", rs.getString("valor_multa")));
            }

            return result.toString();
        } catch (SQLException e) {
            e.printStackTrace();
            return "Erro ao obter dados do veículo.";
        }
    }
}
