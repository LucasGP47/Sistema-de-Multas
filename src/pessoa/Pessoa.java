package pessoa;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;


import conexao_SQL.Conexao_SQL;

public class Pessoa {
	
	private String CNH;

	private String cpf;
	
	public boolean pesquisarNome (String name) {
		
		try {
			Connection con = Conexao_SQL.run_connection();
			String usuario  = "select * from pessoa_fisica where nome=?";
			PreparedStatement stmt = con.prepareStatement(usuario);
			stmt.setString(1, name);
			
			ResultSet rs = stmt.executeQuery();
			
			if (rs.next()) {
				this.cpf = rs.getString("cpf");
				return true;
			} else {
				return false;
			}
			
		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		}
	}
	
	public String status(String name) {
		try {
			Connection con = Conexao_SQL.run_connection();
			String query = "SELECT * FROM pessoa_fisica WHERE nome=?";
			PreparedStatement stmt = con.prepareStatement(query);
			stmt.setString(1, name);
			
			ResultSet rs = stmt.executeQuery();
			
			String result = "";
			
			if (rs.next()) {
				 result += "\n//////////////////////////////////\n";
				 result += String.format("Nome: %s \n", rs.getString("nome"));
				 result += String.format("CPF: %s \n", rs.getString("cpf"));
				 result += String.format("CNH: %s \n", rs.getString("cnh"));
				
				return result;
			} else {
				return "Nenhum resultado encontrado.";
			}
			
		} catch (SQLException e) {
			e.printStackTrace();
			return "Erro ao obter dados.";
		}
	}
	
	public String getCPF() {
        return cpf;
    }

    public void setCPF(String cpf) {
        this.cpf = cpf;
    }
    
    public String getCNH() {
		return CNH;
	}

	public void setCNH(String cNH) {
		CNH = cNH;
	}

	public String getCpf() {
		return cpf;
	}

	public void setCpf(String cpf) {
		this.cpf = cpf;
	}

}
