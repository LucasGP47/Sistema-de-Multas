package pessoa;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import conexao_SQL.Conexao_SQL;

public class Multas {

    private Carteira carteira; 

    public Multas(Carteira carteira) {
        this.carteira = carteira;
    }

    public String consultarMulta(String cpf, String placa) {
        try {
            Connection con = Conexao_SQL.run_connection();
            String query = "SELECT * FROM multas WHERE cpf = ? AND placa_veiculo = ?";
            PreparedStatement stmt = con.prepareStatement(query);
            stmt.setString(1, cpf);
            stmt.setString(2, placa);

            ResultSet rs = stmt.executeQuery();

            StringBuilder result = new StringBuilder();

            if (rs.next()) {
                result.append("\n//////////////////////////////////\n");
                result.append(String.format("CPF: %s \n", rs.getString("cpf")));
                result.append(String.format("Placa do Veículo: %s \n", rs.getString("placa_veiculo")));
                result.append(String.format("Valor da Multa: %s \n", rs.getString("valor")));
                result.append(String.format("Data da Multa: %s \n", rs.getString("data_aplicacao")));
                result.append(String.format("Motivo da multa: %s \n", rs.getString("descricao")));
            } else {
                result.append("Nenhuma multa encontrada para o CPF e placa fornecidos.");
            }

            return result.toString();
        } catch (SQLException e) {
            e.printStackTrace();
            return "Erro ao obter dados da multa.";
        }
    }
    
    public double obterValorMulta(String cpf, String placa) {
        try {
            Connection con = Conexao_SQL.run_connection();
            String query = "SELECT valor FROM multas WHERE cpf = ? AND placa_veiculo = ?";
            PreparedStatement stmt = con.prepareStatement(query);
            stmt.setString(1, cpf);
            stmt.setString(2, placa);

            ResultSet rs = stmt.executeQuery();

            double valorMulta = 0;
            if (rs.next()) {
                valorMulta = rs.getDouble("valor");
            }

            return valorMulta;
        } catch (SQLException e) {
            e.printStackTrace();
            return 0;
        }
    }
}
